import { Injectable, Output, EventEmitter } from '@angular/core';
import { environment } from '../../environment'

declare var sjcl: any;

@Injectable()
export class GameService {
  private _usernameKey: string = 'username';
  private _playerIdKey: string = 'player-id';
  private _playerScoreKey: string = 'player-score';
  private _playerFinalScoreKey: string = 'player-final-score';
  private _playerTeamKey: string = 'player-team';
  private _canaryKey: string = 'canary';
  private _demoDeviceKey: string = 'demo';
  private _achievementsKey: string = 'achievements';
  private _gameIdKey: string = 'game-id';
  private _selfieTakenKey: string = 'selfie-taken';
  private _teamsArray: Array<string> = ['Orange', 'Teal', 'Violet', 'Green'];
  private _teamsClassArray: Array<string> = ['team-orange', 'team-teal', 'team-violet', 'team-green'];
  private _reconnectInterval: number = 5000;

  ws: any;
  socketClosed: boolean = false;
  reconnecting: boolean = false;
  currentState: string = 'title';
  currentSelfieState: string = 'closed';
  selfieAdmin: boolean = false;
  teamScore: number = 0;
  playerUsername: string = localStorage.getItem(this._usernameKey);
  playerScore: number = parseInt(localStorage.getItem(this._playerScoreKey), 10) || 0;
  playerFinalScore: number = parseInt(localStorage.getItem(this._playerFinalScoreKey), 10) || 0;;
  playerId: string = localStorage.getItem(this._playerIdKey);
  playerTeam: any = JSON.parse(localStorage.getItem(this._playerTeamKey)) || null;
  canary: boolean = JSON.parse(localStorage.getItem(this._canaryKey)) || false;
  demoDevice: boolean = JSON.parse(localStorage.getItem(this._demoDeviceKey)) || false;
  achievements: Array<any> = JSON.parse(localStorage.getItem(this._achievementsKey)) || [];
  gameId: string = localStorage.getItem(this._gameIdKey) || null;
  selfieTaken: boolean = JSON.parse(localStorage.getItem(this._selfieTakenKey)) || false;
  achievementIconsHash: any = {
    pops1: 'local_play',
    pops2: 'whatshot',
    pops3: 'sentiment_very_satisfied',
    score1: 'star_border',
    score2: 'star_half',
    score3: 'star',
    gold: 'golden'
  };
  configuration: Object = {};  
  socketUrl: string = (environment.production) ? 'wss://gameserver-game.apps.aws.burrsutter.org/game' : 'ws://localhost:8080/game';
  // socketUrl: string = (environment.production) ? '' : 'wss://gameserver-game.apps.gcp.burrsutter.dev/game';
  @Output() stateChange = new EventEmitter();
  @Output() configurationChange = new EventEmitter();
  @Output() achievementsChange = new EventEmitter();

  constructor() {
    this.connect();
  }

  connect() {
    if (location.search.indexOf('canary=true') > -1 || this.canary) {
      this.canary = true;
      localStorage.setItem(this._canaryKey, 'true');

      // this will be whatever we need the canary to connect to
      this.ws = new WebSocket(this.socketUrl);
    } else {
      this.ws = new WebSocket(this.socketUrl);
    }

    if (location.search.indexOf('demo=true') > -1 || this.demoDevice) {
      this.demoDevice = true;
      localStorage.setItem(this._demoDeviceKey, 'true');
    }

    if (location.search.indexOf('selfie=true') > -1) {
      this.selfieAdmin = true;
    }

    this.ws.onopen = this.onOpen.bind(this);
    this.ws.onclose = this.onClose.bind(this);
    this.ws.onmessage = this.onMessage.bind(this);
  }

  sendMessage(message: any) {
    console.log("sendMessage: " + JSON.stringify(message));
    this.ws.send(JSON.stringify(message));
  }

  incrementPlayerScore(score: number) {
    console.log("incrementPlayerScore");
    this.playerScore += score;
    localStorage.setItem(this._playerScoreKey, JSON.stringify(this.playerScore));    
    
    this.sendMessage({
      type: 'score',
      score: this.playerScore,
      encryptedScore: sjcl.encrypt(''+this.playerId, ''+this.playerScore),
      consecutive: 0,
      goldenSnitchPopped: false
    });
  }

  updatePlayerScore(score: number) {
    this.playerScore = score;
    localStorage.setItem(this._playerScoreKey, JSON.stringify(score));
  }

  resetPlayerScore() {
    this.updatePlayerScore(0);
    this.playerFinalScore = 0;
    localStorage.setItem(this._playerFinalScoreKey, JSON.stringify(this.playerFinalScore));
  }

  resetAchievements() {
    this.achievements = [];
    localStorage.removeItem(this._achievementsKey);
  }

  setDemoDevice(value: boolean) {
    if (value) {
      this.demoDevice = true;
      localStorage.setItem(this._demoDeviceKey, 'true');
    } else {
      this.demoDevice = false;
      localStorage.removeItem(this._demoDeviceKey);
    }
  }

  setCanary(value: boolean) {
    if (value) {
      this.canary = true;
      localStorage.setItem(this._canaryKey, 'true');

      window.location.href = `${window.location.href}?canary=true`;
    } else {
      this.canary = false;
      localStorage.removeItem(this._canaryKey);

      window.location.reload();
    }
  }

  setSelfieTaken(value: boolean) {
    this.selfieTaken = value;
    localStorage.setItem(this._selfieTakenKey, JSON.stringify(value));
  }

  private updateAchievements(achievement) {
    const achievementParts = achievement.description.split('!');

    if (achievementParts.length) {
      achievement.desc = achievementParts[0];
      achievement.text = achievementParts[1];
    } else {
      achievement.desc = achievement.description;
    }
    
    this.achievements.push(achievement);
    localStorage.setItem(this._achievementsKey, JSON.stringify(this.achievements));
    this.achievementsChange.emit(achievement);

    console.log("Achievement Bonus! " + achievement.bonus);
    this.playerScore = this.playerScore + achievement.bonus;
    localStorage.setItem(this._playerScoreKey, JSON.stringify(this.playerScore));
    
  }

  private onOpen(evt) {
    this.socketClosed = false;

    const message = {
      type: 'register'
    };

    if (this.playerId) {
      message['id'] = this.playerId;
    }

    if (this.playerTeam) {
      message['team'] = this.playerTeam.number;
    }

    if (this.playerUsername) {
      message['username'] = this.playerUsername;
    }
    // console.log("onOpen: " + JSON.stringify(message));
    this.ws.send(JSON.stringify(message));
  }

  private onClose(evt) {
    setTimeout(() => {
      this.reconnecting = false;
      this.socketClosed = true;
    }, 500);
  }

  private onMessage(evt) {
    const data = JSON.parse(evt.data);
    console.log("onMessage: " + evt.data);

    if (data.type === 'state') {
      console.log("onMessage: state=" + data.state);
      this.currentState = data.state;
      this.stateChange.emit({
        state: this.currentState
      });

      if (this.currentState === 'start-game') {
        this.resetPlayerScore();
        this.resetAchievements();
        this.teamScore = 0;
      }

      if (this.currentState === 'game-over') {
        if (this.playerScore) {
          this.playerFinalScore = this.playerScore;
          localStorage.setItem(this._playerFinalScoreKey, JSON.stringify(this.playerFinalScore));
        }

        this.updatePlayerScore(0);
      }

      return;
    }

    if (data.type === 'selfie-state') {
      this.currentSelfieState = data.state;
      return;
    }

    if (data.type === 'team-score') {
      this.teamScore = data.score;
      return;
    }

    if (data.type === 'id') {
      this.playerId = data.id;
      localStorage.setItem(this._playerIdKey, data.id);
    }

    if (data.type === 'configuration') {
      console.log("onMessage: configuration=" + JSON.stringify(data.configuration));
      if (data.username) {
        localStorage.setItem(this._usernameKey, data.username);
        this.playerUsername = data.username;
      }

      if (data.team) {
        let team = {
          name: this._teamsArray[data.team - 1],
          class: this._teamsClassArray[data.team - 1],
          number: data.team
        };

        localStorage.setItem(this._playerTeamKey, JSON.stringify(team));
        this.playerTeam = team;
      }

      if (data.locationKey) {
        console.log("\n\n LOCATION: " + data.locationKey + "\n\n");
      }

      this.configuration = data.configuration;

      if (data.configuration && data.configuration.gameId) {
        if (this.gameId && this.gameId !== data.configuration.gameId) {
          this.resetPlayerScore();
          this.resetAchievements();
          this.setSelfieTaken(false);
          this.teamScore = 0;
        }

        this.gameId = data.configuration.gameId;
        localStorage.setItem(this._gameIdKey, data.configuration.gameId);
      }

      if (this.demoDevice && data.configuration.bp) {
        return;
      }

      this.configurationChange.emit({
        configuration: this.configuration
      });
    }

    if (data.type === 'achievements') {
      data.achievements.forEach(messageAchievement => {
        let found = false;

        this.achievements.forEach(achievement => {
            if (messageAchievement.type === achievement.type) {
              found = true;
            }
        });

        // You can only receive/achieve an achievement one time
        if (found) {
          return;
        }
        

        this.updateAchievements(messageAchievement);
      });
    }
  }

}
