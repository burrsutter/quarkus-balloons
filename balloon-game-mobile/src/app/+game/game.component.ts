import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { AchievementComponent } from './achievement/achievement.component';
import { GameService } from './service/game.service';

declare var Phaser: any;
declare var sjcl: any;
declare var componentHandler: any;

@Component({
  moduleId: module.id,
  selector: 'app-game',
  templateUrl: 'game.component.html',
  styleUrls: ['game.component.css'],
  directives: [AchievementComponent],
})

export class GameComponent implements OnInit, OnDestroy {
  currentState = 'title';
  game;
  teamScore = 0;
  achievements = [];
  goldenSnitch1Created = false;
  goldenSnitch2Created = false;
  goldenSnitchChance = 0.2;
  balloons;
  explosions;
  pointsHash;
  configuration;

  constructor(private gameService: GameService) {
    this.currentState = this.gameService.currentState;
    this.configuration = this.gameService.configuration;

    if (this.configuration.points) {
      this.pointsHash = {
        'balloon_red': this.configuration.points.red,
        'balloon_blue': this.configuration.points.blue,
        'balloon_yellow': this.configuration.points.yellow,
        'balloon_green': this.configuration.points.green,
        'balloon_golden1': this.configuration.points.goldenSnitch1,
        'balloon_golden2': this.configuration.points.goldenSnitch2,
      }
    }

    this.stateChangeHandler = this.stateChangeHandler.bind(this);
    this.configurationChangeHandler = this.configurationChangeHandler.bind(this);

    this.gameService.stateChange.subscribe(this.stateChangeHandler);
    this.gameService.configurationChange.subscribe(this.configurationChangeHandler);
  }

  ngOnInit() {
    this.game = new Phaser.Game(window.innerWidth, window.innerHeight - 56, Phaser.AUTO, 'game', null, true);

    const fireRate = 100;
    // Burr: const numBalloons = 4;
    const numBalloons = 5;
    const balloonRotationSpeed = 100;
    let nextFire = 0;
    let consecutive = 0;

    let TitleState = {
      create: () => {
        this.game.stage.disableVisibilityChange = true;
      }
    };

    let PlayState = {
      preload: () => {
        // Burr: this.game.load.atlas('balloons', './app/+game/assets/balloons.png', './app/+game/assets/balloons.json');
        this.game.load.atlas('balloons', './app/+game/assets/balloons_v2.png', './app/+game/assets/balloons_v2.json');
        this.game.load.spritesheet('explosion', './app/+game/assets/explosion.png', 128, 128, 10);
      },
      create: () => {
        this.game.stage.disableVisibilityChange = true;
        this.game.physics.arcade.gravity.y = 500;

        this.balloons = this.game.add.group();
        this.balloons.enableBody = true;
        this.balloons.physicsBodyType = Phaser.Physics.ARCADE;

        for (var i = 0; i < numBalloons; i += 1) {
          this.balloons.create(0, 0, 'balloons', i, false);
        }

        this.balloons.setAll('checkWorldBounds', true);
        this.balloons.setAll('outOfBoundsKill', true);
        this.balloons.setAll('blendMode', Phaser.blendModes.OVERLAY);
        this.balloons.setAll('alpha', this.configuration.opacity);

        this.balloons.children.forEach(PlayState.setupBalloon);

        this.explosions = this.game.add.group();
        this.explosions.createMultiple(4, 'explosion');
        this.explosions.children.forEach(explosion => {
          explosion.anchor.set(0.5);
          explosion.animations.add('explode');
        });

        PlayState.throwObject();
      },
      update: () => {
        PlayState.throwObject();
      },
      setupBalloon: balloon => {
        balloon.checkWorldBounds = true;
        balloon.outOfBoundsKill = true;
        balloon.scale.setTo(this.configuration.scale);
        balloon.anchor.setTo(0.5);
        balloon.inputEnabled = true;
        balloon.events.onOutOfBounds.add(() => {
          if (balloon.frameName === 'balloon_golden1') {
            this.balloons.remove(balloon);
            this.goldenSnitch1Created = false;
          }
          if (balloon.frameName === 'balloon_golden2') {
            this.balloons.remove(balloon);
            this.goldenSnitch2Created = false;
          }
          consecutive = 0;
        });

        balloon.events.onInputDown.add(evt => {
          balloon.kill();

          if (balloon.frameName === 'balloon_golden1') {
            this.balloons.remove(balloon);
            this.goldenSnitch1Created = false;
          }
          if (balloon.frameName === 'balloon_golden2') {
            this.balloons.remove(balloon);
            this.goldenSnitch2Created = false;
          }


          consecutive += 1;
          this.gameService.playerScore += this.pointsHash[balloon.frameName];

          var explosion = this.explosions.getFirstExists(false);
          explosion.reset(evt.world.x, evt.world.y);
          explosion.play('explode', 30, false, true);

          // Burr: this is the message payload per pop
          this.gameService.sendMessage({
            type: 'score',
            score: this.gameService.playerScore,
            playerId: this.gameService.playerId,
            playerName: this.gameService.playerUsername,
            // encryptedScore: sjcl.encrypt(''+this.gameService.playerId, ''+this.gameService.playerScore),
            consecutive: consecutive,
            goldenSnitch1Popped: (balloon.frameName === 'balloon_golden1') ? true : false,
            goldenSnitch2Popped: (balloon.frameName === 'balloon_golden2') ? true : false,
            balloonType: balloon.frameName
          });

          this.gameService.updatePlayerScore(this.gameService.playerScore);

          // purely for demo purposes
          if (consecutive % 5 === 0) {
            this.achievements = [{
              type: 'consecutive',
              message: `Nice job! ${consecutive} pops in a row!`
            }];
          }
        });
      },
      throwObject: () => {
        if (this.game.time.now > nextFire && this.balloons.countDead() > 0) {
          if (this.balloons.countDead() > 0) {
            nextFire = this.game.time.now + fireRate;
            PlayState.throwGoodObject();
          }
        }
      },
      throwGoodObject: () => {
        let obj;
        
        if (this.configuration.goldenSnitch1 && Math.random() < this.goldenSnitchChance && !this.goldenSnitch1Created) {
          console.log("creating Burr"); 
          obj = this.balloons.create(0, 0, 'balloons', 4, false);
          PlayState.setupBalloon(obj);
          
         this.goldenSnitch1Created = true;
        } else {
          obj = this.balloons.getFirstDead();
        }

        if (this.configuration.goldenSnitch2 && Math.random() < this.goldenSnitchChance && !this.goldenSnitch2Created) {
          console.log("creating Ray");          
          obj = this.balloons.create(0, 0, 'balloons', 5, false);
          PlayState.setupBalloon(obj);
          
         this.goldenSnitch2Created = true;
        } else {
          obj = this.balloons.getFirstDead();
        }


        obj.reset(this.game.world.centerX + this.game.rnd.integerInRange(-75, 75), this.game.world.height);
        obj.body.angularVelocity = (Math.random() - 0.5) * balloonRotationSpeed;

        if (this.configuration.opacity) {
          obj.alpha = this.configuration.opacity / 100;
        }

        if (this.configuration.scale) {
          obj.scale.x = this.configuration.scale;
          obj.scale.y = this.configuration.scale;
        }

        const speed = (this.game.world.height + 56 - 568) * 0.5 + (575 + (this.configuration.speed - 50) * 5);

        this.game.physics.arcade.moveToXY(obj, this.game.world.centerX + this.game.rnd.integerInRange(-50, 50), this.game.world.centerY, speed);
      }
    };

    let GameOverState = {
      create: () => {}
    };

    this.game.state.add('Title', TitleState);
    this.game.state.add('Play', PlayState);
    this.game.state.add('GameOver', GameOverState);
    // this.game.state.start('Title');

    this.stateChangeHandler({
      state: this.currentState
    });

    // PlayState.throwGoodObject = PlayState.throwGoodObject.bind(this);
  }

  stateChangeHandler(evt) {
    this.currentState = evt.state;

    switch (evt.state) {
      case 'title':
        this.game.state.start('Title');
        this.game.paused = false;
        break;

      case 'play':
        if (this.game.state.current !== 'Play') {
          this.game.state.start('Play');
        }

        this.game.paused = false;
        break;

      case 'pause':
        if (this.game.isRunning) {
          this.game.paused = true;
        }

        break;

      case 'game-over':
        this.game.state.start('GameOver');
        this.game.paused = false;
        break;
    }
  }

  configurationChangeHandler(evt) {
    this.configuration = evt.configuration;

    if (this.configuration.points) {
      this.pointsHash = {
        'balloon_red': this.configuration.points.red,
        'balloon_blue': this.configuration.points.blue,
        'balloon_yellow': this.configuration.points.yellow,
        'balloon_green': this.configuration.points.green,
        'balloon_golden1': this.configuration.points.goldenSnitch1,
        'balloon_golden2': this.configuration.points.goldenSnitch2,
      }
    }
  }

  reconnect() {
    this.gameService.reconnecting = true;
    this.gameService.connect();

    setTimeout(() => {
      componentHandler.upgradeDom();
    }, 0);
  }

  ngOnDestroy() {
    this.game.destroy();
  }

  nameLoaded(event) {
    event.target.classList.add('loaded');
    event.target.classList.remove('loading');
  }
}
