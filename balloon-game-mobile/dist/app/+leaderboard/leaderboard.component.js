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
var http_1 = require('@angular/http');
var game_service_1 = require('../+game/service/game.service');
require('rxjs/add/operator/toPromise');
var LeaderboardComponent = (function () {
    function LeaderboardComponent(http, gameService) {
        this.http = http;
        this.gameService = gameService;
        this.error = false;
        // let promises = [];
        //
        // promises.push(this.getAchievements());
        //
        // this.playerId = localStorage.getItem('player-id');
        //
        // if (this.playerId) {
        //   promises.push(this.getPlayerAchievements(this.playerId));
        // }
        //
        // Promise.all(promises)
        //   .then(responses => {
        //     /*
        //      * responses will either have 1 or 2 items in the array
        //      *
        //      * we'll get two responses if we have a player id in localstorage
        //      * when the page loads
        //      *
        //      * responses[0] will be all of the achievements
        //      * if there is a player id, responses[1] will be the player's achievements
        //      */
        //     this.achievements = responses[0];
        //
        //     if (responses[1]) {
        //       this.playerAchievements = responses[1];
        //       this.achievements.forEach(achievement => {
        //         this.playerAchievements.forEach(playerAchievement => {
        //           if (achievement.achievementType === playerAchievement.achievementType) {
        //             achievement.achieved = playerAchievement.achieved;
        //           }
        //         });
        //       });
        //     }
        //   }, () => {
        //     console.log('there was an error');
        //   });
    }
    LeaderboardComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'app-leaderboard',
            templateUrl: 'leaderboard.component.html',
            styleUrls: ['leaderboard.component.css']
        }), 
        __metadata('design:paramtypes', [http_1.Http, game_service_1.GameService])
    ], LeaderboardComponent);
    return LeaderboardComponent;
}());
exports.LeaderboardComponent = LeaderboardComponent;
//# sourceMappingURL=leaderboard.component.js.map