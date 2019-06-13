import { Component, OnInit } from '@angular/core';
import { Routes, Route, Router, ROUTER_DIRECTIVES} from '@angular/router';
import { GameComponent } from './+game';
import { SelfieComponent } from './+selfie';
import { HomeComponent } from './+home';
import { LeaderboardComponent } from './+leaderboard';
import { WinnerComponent } from './winner/winner.component';
import { GameService } from './+game/service/game.service';

declare var componentHandler: any;

@Component({
  moduleId: module.id,
  selector: 'rh-keynote-demo-app',
  templateUrl: 'rh-keynote-demo.component.html',
  styleUrls: ['rh-keynote-demo.component.css'],
  directives: [ROUTER_DIRECTIVES, WinnerComponent],
  providers: [GameService]
})

@Routes([
  {path: '/game', component: GameComponent},
  {path: '/playerid', component: SelfieComponent},
  {path: '/achievements', component: LeaderboardComponent},
  {path: '/', component: HomeComponent},
  {path: '*', component: HomeComponent}
])

export class RhKeynoteDemoAppComponent implements OnInit {
  title: String = 'Balloon Mania';

  constructor(private router: Router, private gameService: GameService) {}

  ngOnInit() {
    componentHandler.upgradeDom();
  }

  closeDrawer() {
    const matches = document.querySelectorAll('.mdl-layout__drawer, .mdl-layout__obfuscator');
    [].forEach.call(matches, element => {
      element.classList.remove('is-visible');
    });
  }
}
