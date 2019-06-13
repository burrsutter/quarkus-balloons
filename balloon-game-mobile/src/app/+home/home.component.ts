import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES} from '@angular/router';
import { GameService } from '../+game/service/game.service';

@Component({
  moduleId: module.id,
  selector: 'app-home',
  templateUrl: 'home.component.html',
  styleUrls: ['home.component.css'],
  directives: [ROUTER_DIRECTIVES]
})
export class HomeComponent {

  constructor(private gameService: GameService) {}

}
