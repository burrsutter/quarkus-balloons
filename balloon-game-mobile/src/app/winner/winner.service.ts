import { Injectable } from '@angular/core';

@Injectable()
export class WinnerService {
  ws;
  connected: boolean = false;

  constructor() {}

  connect(newPlayerId?: string) {
    if (newPlayerId && this.connected) {
      this.ws.close();
      console.log('in here');
      this.connected = false;
    }

    const playerId = localStorage.getItem('player-id');

    if (!playerId || this.connected) {
      return;
    }

    this.connected = true;

    this.ws = new WebSocket('ws://localhost:8081/game/winner');
    this.ws.onopen = event => {
      console.log('connected to game winner');
      this.ws.send(playerId);
    }

    this.ws.onmessage = event => {
      if (!event.data) {
        return;
      }

      console.log(event);

      try {
        const data = JSON.parse(event.data);

        if (data.type === 'winner') {
          console.log('You are a winner');
        }
      }
      catch (error) {

      }
    }
  }

}
