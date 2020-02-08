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
var achievement_component_1 = require('./achievement/achievement.component');
var game_service_1 = require('./service/game.service');
var GameComponent = (function () {
    function GameComponent(gameService) {
        this.gameService = gameService;
        this.currentState = 'title';
        this.teamScore = 0;
        this.achievements = [];
        this.goldenSnitch1Created = false;
        this.goldenSnitch2Created = false;
        this.goldenSnitchChance = 0.2;
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
            };
        }
        this.stateChangeHandler = this.stateChangeHandler.bind(this);
        this.configurationChangeHandler = this.configurationChangeHandler.bind(this);
        this.gameService.stateChange.subscribe(this.stateChangeHandler);
        this.gameService.configurationChange.subscribe(this.configurationChangeHandler);
    }
    GameComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.game = new Phaser.Game(window.innerWidth, window.innerHeight - 56, Phaser.AUTO, 'game', null, true);
        var fireRate = 100;
        // Burr: const numBalloons = 4;
        var numBalloons = 5;
        var balloonRotationSpeed = 100;
        var nextFire = 0;
        var consecutive = 0;
        var TitleState = {
            create: function () {
                _this.game.stage.disableVisibilityChange = true;
            }
        };
        var PlayState = {
            preload: function () {
                // Burr: this.game.load.atlas('balloons', './app/+game/assets/balloons.png', './app/+game/assets/balloons.json');
                _this.game.load.atlas('balloons', './app/+game/assets/balloons_v2.png', './app/+game/assets/balloons_v2.json');
                _this.game.load.spritesheet('explosion', './app/+game/assets/explosion.png', 128, 128, 10);
            },
            create: function () {
                _this.game.stage.disableVisibilityChange = true;
                _this.game.physics.arcade.gravity.y = 500;
                _this.balloons = _this.game.add.group();
                _this.balloons.enableBody = true;
                _this.balloons.physicsBodyType = Phaser.Physics.ARCADE;
                for (var i = 0; i < numBalloons; i += 1) {
                    _this.balloons.create(0, 0, 'balloons', i, false);
                }
                _this.balloons.setAll('checkWorldBounds', true);
                _this.balloons.setAll('outOfBoundsKill', true);
                _this.balloons.setAll('blendMode', Phaser.blendModes.OVERLAY);
                _this.balloons.setAll('alpha', _this.configuration.opacity);
                _this.balloons.children.forEach(PlayState.setupBalloon);
                _this.explosions = _this.game.add.group();
                _this.explosions.createMultiple(4, 'explosion');
                _this.explosions.children.forEach(function (explosion) {
                    explosion.anchor.set(0.5);
                    explosion.animations.add('explode');
                });
                PlayState.throwObject();
            },
            update: function () {
                PlayState.throwObject();
            },
            setupBalloon: function (balloon) {
                balloon.checkWorldBounds = true;
                balloon.outOfBoundsKill = true;
                balloon.scale.setTo(_this.configuration.scale);
                balloon.anchor.setTo(0.5);
                balloon.inputEnabled = true;
                balloon.events.onOutOfBounds.add(function () {
                    if (balloon.frameName === 'balloon_golden1') {
                        _this.balloons.remove(balloon);
                        _this.goldenSnitch1Created = false;
                    }
                    if (balloon.frameName === 'balloon_golden2') {
                        _this.balloons.remove(balloon);
                        _this.goldenSnitch2Created = false;
                    }
                    consecutive = 0;
                });
                balloon.events.onInputDown.add(function (evt) {
                    balloon.kill();
                    if (balloon.frameName === 'balloon_golden1') {
                        _this.balloons.remove(balloon);
                        _this.goldenSnitch1Created = false;
                    }
                    if (balloon.frameName === 'balloon_golden2') {
                        _this.balloons.remove(balloon);
                        _this.goldenSnitch2Created = false;
                    }
                    consecutive += 1;
                    _this.gameService.playerScore += _this.pointsHash[balloon.frameName];
                    var explosion = _this.explosions.getFirstExists(false);
                    explosion.reset(evt.world.x, evt.world.y);
                    explosion.play('explode', 30, false, true);
                    // Burr: this is the message payload per pop
                    _this.gameService.sendMessage({
                        type: 'score',
                        score: _this.gameService.playerScore,
                        playerId: _this.gameService.playerId,
                        playerName: _this.gameService.playerUsername,
                        // encryptedScore: sjcl.encrypt(''+this.gameService.playerId, ''+this.gameService.playerScore),
                        consecutive: consecutive,
                        goldenSnitch1Popped: (balloon.frameName === 'balloon_golden1') ? true : false,
                        goldenSnitch2Popped: (balloon.frameName === 'balloon_golden2') ? true : false,
                        balloonType: balloon.frameName
                    });
                    _this.gameService.updatePlayerScore(_this.gameService.playerScore);
                    // purely for demo purposes
                    if (consecutive % 5 === 0) {
                        _this.achievements = [{
                                type: 'consecutive',
                                message: "Nice job! " + consecutive + " pops in a row!"
                            }];
                    }
                });
            },
            throwObject: function () {
                if (_this.game.time.now > nextFire && _this.balloons.countDead() > 0) {
                    if (_this.balloons.countDead() > 0) {
                        nextFire = _this.game.time.now + fireRate;
                        PlayState.throwGoodObject();
                    }
                }
            },
            throwGoodObject: function () {
                var obj;
                if (_this.configuration.goldenSnitch1 && Math.random() < _this.goldenSnitchChance && !_this.goldenSnitch1Created) {
                    console.log("creating Burr");
                    obj = _this.balloons.create(0, 0, 'balloons', 4, false);
                    PlayState.setupBalloon(obj);
                    _this.goldenSnitch1Created = true;
                }
                else {
                    obj = _this.balloons.getFirstDead();
                }
                if (_this.configuration.goldenSnitch2 && Math.random() < _this.goldenSnitchChance && !_this.goldenSnitch2Created) {
                    console.log("creating Ray");
                    obj = _this.balloons.create(0, 0, 'balloons', 5, false);
                    PlayState.setupBalloon(obj);
                    _this.goldenSnitch2Created = true;
                }
                else {
                    obj = _this.balloons.getFirstDead();
                }
                obj.reset(_this.game.world.centerX + _this.game.rnd.integerInRange(-75, 75), _this.game.world.height);
                obj.body.angularVelocity = (Math.random() - 0.5) * balloonRotationSpeed;
                if (_this.configuration.opacity) {
                    obj.alpha = _this.configuration.opacity / 100;
                }
                if (_this.configuration.scale) {
                    obj.scale.x = _this.configuration.scale;
                    obj.scale.y = _this.configuration.scale;
                }
                var speed = (_this.game.world.height + 56 - 568) * 0.5 + (575 + (_this.configuration.speed - 50) * 5);
                _this.game.physics.arcade.moveToXY(obj, _this.game.world.centerX + _this.game.rnd.integerInRange(-50, 50), _this.game.world.centerY, speed);
            }
        };
        var GameOverState = {
            create: function () { }
        };
        this.game.state.add('Title', TitleState);
        this.game.state.add('Play', PlayState);
        this.game.state.add('GameOver', GameOverState);
        // this.game.state.start('Title');
        this.stateChangeHandler({
            state: this.currentState
        });
        // PlayState.throwGoodObject = PlayState.throwGoodObject.bind(this);
    };
    GameComponent.prototype.stateChangeHandler = function (evt) {
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
    };
    GameComponent.prototype.configurationChangeHandler = function (evt) {
        this.configuration = evt.configuration;
        if (this.configuration.points) {
            this.pointsHash = {
                'balloon_red': this.configuration.points.red,
                'balloon_blue': this.configuration.points.blue,
                'balloon_yellow': this.configuration.points.yellow,
                'balloon_green': this.configuration.points.green,
                'balloon_golden1': this.configuration.points.goldenSnitch1,
                'balloon_golden2': this.configuration.points.goldenSnitch2,
            };
        }
    };
    GameComponent.prototype.reconnect = function () {
        this.gameService.reconnecting = true;
        this.gameService.connect();
        setTimeout(function () {
            componentHandler.upgradeDom();
        }, 0);
    };
    GameComponent.prototype.ngOnDestroy = function () {
        this.game.destroy();
    };
    GameComponent.prototype.nameLoaded = function (event) {
        event.target.classList.add('loaded');
        event.target.classList.remove('loading');
    };
    GameComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'app-game',
            templateUrl: 'game.component.html',
            styleUrls: ['game.component.css'],
            directives: [achievement_component_1.AchievementComponent],
        }), 
        __metadata('design:paramtypes', [game_service_1.GameService])
    ], GameComponent);
    return GameComponent;
}());
exports.GameComponent = GameComponent;
//# sourceMappingURL=game.component.js.map