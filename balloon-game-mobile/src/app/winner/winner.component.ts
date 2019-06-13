import { Component } from '@angular/core';

@Component({
  moduleId: module.id,
  selector: 'app-winner',
  templateUrl: 'winner.component.html',
  styleUrls: ['winner.component.css']
})

export class WinnerComponent {
  winner: boolean = false;

  constructor() {}

}
