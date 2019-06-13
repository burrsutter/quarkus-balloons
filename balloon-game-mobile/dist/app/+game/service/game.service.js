"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var environment_1 = require('../../environment');
var GameService = (function () {
    function GameService() {
        this._usernameKey = 'username';
        this._playerIdKey = 'player-id';
        this._playerScoreKey = 'player-score';
        this._playerFinalScoreKey = 'player-final-score';
        this._playerTeamKey = 'player-team';
        this._canaryKey = 'canary';
        this._demoDeviceKey = 'demo';
        this._achievementsKey = 'achievements';
        this._gameIdKey = 'game-id';
        this._selfieTakenKey = 'selfie-taken';
        this._teamsArray = ['Orange', 'Teal', 'Violet', 'Green'];
        this._teamsClassArray = ['team-orange', 'team-teal', 'team-violet', 'team-green'];
        this._reconnectInterval = 5000;
        this.socketClosed = false;
        this.reconnecting = false;
        this.currentState = 'title';
        this.currentSelfieState = 'closed';
        this.selfieAdmin = false;
        this.teamScore = 0;
        this.playerUsername = localStorage.getItem(this._usernameKey);
        this.playerScore = parseInt(localStorage.getItem(this._playerScoreKey), 10) || 0;
        this.playerFinalScore = parseInt(localStorage.getItem(this._playerFinalScoreKey), 10) || 0;
        this.playerId = localStorage.getItem(this._playerIdKey);
        this.playerTeam = JSON.parse(localStorage.getItem(this._playerTeamKey)) || null;
        this.canary = JSON.parse(localStorage.getItem(this._canaryKey)) || false;
        this.demoDevice = JSON.parse(localStorage.getItem(this._demoDeviceKey)) || false;
        this.achievements = JSON.parse(localStorage.getItem(this._achievementsKey)) || [];
        this.gameId = localStorage.getItem(this._gameIdKey) || null;
        this.selfieTaken = JSON.parse(localStorage.getItem(this._selfieTakenKey)) || false;
        this.achievementIconsHash = {
            pops1: 'local_play',
            pops2: 'whatshot',
            pops3: 'sentiment_very_satisfied',
            score1: 'star_border',
            score2: 'star_half',
            score3: 'star',
            gold: 'golden'
        };
        this.configuration = {};
        this.socketUrl = (environment_1.environment.production) ? 'ws://gamebus-production.apps-test.redhatkeynote.com/game' : 'ws://localhost:8080/game';
        // socketUrl: string = (environment.production) ? '' : 'ws://demo.burr.red:9001/game';
        this.stateChange = new core_1.EventEmitter();
        this.configurationChange = new core_1.EventEmitter();
        this.achievementsChange = new core_1.EventEmitter();
        this.connect();
    }
    ;
    GameService.prototype.connect = function () {
        if (location.search.indexOf('canary=true') > -1 || this.canary) {
            this.canary = true;
            localStorage.setItem(this._canaryKey, 'true');
            // this will be whatever we need the canary to connect to
            this.ws = new WebSocket(this.socketUrl);
        }
        else {
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
    };
    GameService.prototype.sendMessage = function (message) {
        // console.log("sendMessage: " + JSON.stringify(message));
        this.ws.send(JSON.stringify(message));
    };
    GameService.prototype.incrementPlayerScore = function (score) {
        this.playerScore += score;
        localStorage.setItem(this._playerScoreKey, JSON.stringify(this.playerScore));
        // BURR
        // console.log("incrementPlayerScore");
        this.sendMessage({
            type: 'score',
            score: this.playerScore,
            encryptedScore: sjcl.encrypt('' + this.playerId, '' + this.playerScore),
            consecutive: 0,
            goldenSnitchPopped: false
        });
    };
    GameService.prototype.updatePlayerScore = function (score) {
        this.playerScore = score;
        localStorage.setItem(this._playerScoreKey, JSON.stringify(score));
    };
    GameService.prototype.resetPlayerScore = function () {
        this.updatePlayerScore(0);
        this.playerFinalScore = 0;
        localStorage.setItem(this._playerFinalScoreKey, JSON.stringify(this.playerFinalScore));
    };
    GameService.prototype.resetAchievements = function () {
        this.achievements = [];
        localStorage.removeItem(this._achievementsKey);
    };
    GameService.prototype.setDemoDevice = function (value) {
        if (value) {
            this.demoDevice = true;
            localStorage.setItem(this._demoDeviceKey, 'true');
        }
        else {
            this.demoDevice = false;
            localStorage.removeItem(this._demoDeviceKey);
        }
    };
    GameService.prototype.setCanary = function (value) {
        if (value) {
            this.canary = true;
            localStorage.setItem(this._canaryKey, 'true');
            window.location.href = window.location.href + "?canary=true";
        }
        else {
            this.canary = false;
            localStorage.removeItem(this._canaryKey);
            window.location.reload();
        }
    };
    GameService.prototype.setSelfieTaken = function (value) {
        this.selfieTaken = value;
        localStorage.setItem(this._selfieTakenKey, JSON.stringify(value));
    };
    GameService.prototype.updateAchievements = function (achievement) {
        var achievementParts = achievement.description.split('!');
        if (achievementParts.length) {
            achievement.desc = achievementParts[0];
            achievement.text = achievementParts[1];
        }
        else {
            achievement.desc = achievement.description;
        }
        this.achievements.push(achievement);
        localStorage.setItem(this._achievementsKey, JSON.stringify(this.achievements));
        this.achievementsChange.emit(achievement);
    };
    GameService.prototype.onOpen = function (evt) {
        this.socketClosed = false;
        var message = {
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
        console.log("onOpen: " + JSON.stringify(message));
        this.ws.send(JSON.stringify(message));
    };
    GameService.prototype.onClose = function (evt) {
        var _this = this;
        setTimeout(function () {
            _this.reconnecting = false;
            _this.socketClosed = true;
        }, 500);
    };
    GameService.prototype.onMessage = function (evt) {
        var _this = this;
        var data = JSON.parse(evt.data);
        console.log("onMessage: " + evt.data);
        if (data.type === 'state') {
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
            localStorage.setItem(this._playerIdKey, data.id);
        }
        if (data.type === 'configuration') {
            if (data.username) {
                localStorage.setItem(this._usernameKey, data.username);
                this.playerUsername = data.username;
            }
            if (data.team) {
                var team = {
                    name: this._teamsArray[data.team - 1],
                    class: this._teamsClassArray[data.team - 1],
                    number: data.team
                };
                localStorage.setItem(this._playerTeamKey, JSON.stringify(team));
                this.playerTeam = team;
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
            data.achievements.forEach(function (messageAchievement) {
                var found = false;
                _this.achievements.forEach(function (achievement) {
                    if (messageAchievement.type === achievement.type) {
                        found = true;
                    }
                });
                if (found) {
                    return;
                }
                _this.updateAchievements(messageAchievement);
            });
        }
    };
    __decorate([
        core_1.Output(), 
        __metadata('design:type', Object)
    ], GameService.prototype, "stateChange", void 0);
    __decorate([
        core_1.Output(), 
        __metadata('design:type', Object)
    ], GameService.prototype, "configurationChange", void 0);
    __decorate([
        core_1.Output(), 
        __metadata('design:type', Object)
    ], GameService.prototype, "achievementsChange", void 0);
    GameService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [])
    ], GameService);
    return GameService;
}());
exports.GameService = GameService;
//# sourceMappingURL=game.service.js.map