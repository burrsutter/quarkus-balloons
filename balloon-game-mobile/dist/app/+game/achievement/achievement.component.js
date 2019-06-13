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
var game_service_1 = require('../service/game.service');
var AchievementComponent = (function () {
    function AchievementComponent(_element, gameService) {
        this._element = _element;
        this.gameService = gameService;
        this.achievements = [];
        this.element = _element;
        this.achievementChangeHandler = this.achievementChangeHandler.bind(this);
        this.animationEndHandler = this.animationEndHandler.bind(this);
        this.transitionEndHandler = this.transitionEndHandler.bind(this);
        this.gameService.achievementsChange.subscribe(this.achievementChangeHandler);
        this.element.nativeElement.addEventListener(this.whichAnimationEvent(), this.animationEndHandler);
        this.element.nativeElement.addEventListener(this.whichTransitionEvent(), this.transitionEndHandler);
    }
    AchievementComponent.prototype.achievementChangeHandler = function (achievement) {
        this.achievements.push(achievement);
        this.showAchievement();
    };
    AchievementComponent.prototype.showAchievement = function () {
        console.log('showing achievement');
        this.element.nativeElement.classList.add('visible');
    };
    AchievementComponent.prototype.animationEndHandler = function (evt) {
        console.log('in animation handler');
        if (evt.target.classList.contains('visible')) {
            setTimeout(function () {
                evt.target.classList.remove('visible');
                console.log('animation handler timeout done!');
            }, 3000);
        }
    };
    AchievementComponent.prototype.transitionEndHandler = function (evt) {
        if (evt.target.classList.contains('visible')) {
            return;
        }
        console.log('removing achievement');
        this.achievements.shift();
        if (this.achievements.length > 0) {
            console.log('showing another achievement');
            this.showAchievement();
        }
    };
    AchievementComponent.prototype.whichTransitionEvent = function () {
        var t;
        var el = document.createElement('fakeelement');
        var transitions = {
            'transition': 'transitionend',
            'OTransition': 'oTransitionEnd',
            'MozTransition': 'transitionend',
            'WebkitTransition': 'webkitTransitionEnd'
        };
        for (t in transitions) {
            if (el.style[t] !== undefined) {
                console.log('transition event: ' + transitions[t]);
                return transitions[t];
            }
        }
    };
    AchievementComponent.prototype.whichAnimationEvent = function () {
        var t;
        var el = document.createElement('fakeelement');
        var transitions = {
            'animation': 'animationend',
            'OAnimation': 'oAnimationEnd',
            'MozAnimation': 'animationend',
            'WebkitAnimation': 'webkitAnimationEnd'
        };
        for (t in transitions) {
            if (el.style[t] !== undefined) {
                console.log('animation event: ' + transitions[t]);
                return transitions[t];
            }
        }
    };
    AchievementComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'app-achievement',
            templateUrl: 'achievement.component.html',
            styleUrls: ['achievement.component.css']
        }), 
        __metadata('design:paramtypes', [core_1.ElementRef, game_service_1.GameService])
    ], AchievementComponent);
    return AchievementComponent;
}());
exports.AchievementComponent = AchievementComponent;
//# sourceMappingURL=achievement.component.js.map