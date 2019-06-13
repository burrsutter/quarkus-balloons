import { Component } from '@angular/core';
import { Http } from '@angular/http';
import { GameService } from '../+game/service/game.service';

import 'rxjs/add/operator/toPromise';

@Component({
  moduleId: module.id,
  selector: 'app-leaderboard',
  templateUrl: 'leaderboard.component.html',
  styleUrls: ['leaderboard.component.css']
})
export class LeaderboardComponent {
  error:Boolean = false;

  constructor(private http:Http, private gameService: GameService) {
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

  // getAchievements() {
  //   return this.http.get(`${this.achievementsUrl}`)
  //     .toPromise()
  //     .then(response => response.json());
  // }
  //
  // getPlayerAchievements(playerId:String) {
  //   return this.http.get(`${this.achievementsUrl}/${playerId}`)
  //     .toPromise()
  //     .then(response => response.json());
  // }

}
