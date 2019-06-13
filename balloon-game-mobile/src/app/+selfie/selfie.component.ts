import { Component, ElementRef } from '@angular/core';
import { Http, HTTP_PROVIDERS, Headers, RequestOptions } from '@angular/http';
import { GameService } from '../+game/service/game.service';
import { environment } from '../environment'

declare var loadImage: any;
declare var componentHandler: any;

@Component({
  moduleId: module.id,
  selector: 'app-selfie',
  host: {'class': 'dark-bg'},
  templateUrl: 'selfie.component.html',
  styleUrls: ['selfie.component.css'],
  providers: [ HTTP_PROVIDERS ]
})

export class SelfieComponent {
  canvas;
  uploadBtn;
  imageUploaded: boolean = false;
  error: boolean = false;
  uploading: boolean = false;
  canUpload: boolean = false;
  scoreIncrement: number = 500;
  pointsAwarded: boolean = false;
  uploadUrl: string = (environment.production) ? 'http://player-id-server-demo.apps-test.redhatkeynote.com/upload' : 'http://localhost:8085/upload';

  constructor(private http: Http, private gameService: GameService, private elementRef: ElementRef) {}

  onChange(event) {
    const self = this;

    if (event.target.files.length === 1 && event.target.files[0].type.indexOf('image/') === 0) {
      let file = event.target.files[0];
      let options = {
        maxWidth: 300,
        maxHeight: 300,
        crop: true,
        downsamplingRatio: 0.05
      };

      self.canUpload = true;

      loadImage.parseMetaData(file, data => {
        if (data.exif) {
          options['orientation'] = data.exif.get('Orientation');
        }

        loadImage(file, data => {
          self.canvas = document.getElementById('canvas');
          self.canvas.parentNode.replaceChild(data, this.canvas);
          self.canvas = data;
        }, options);
      });
    }
  }

  upload() {
    if (this.gameService.currentState === 'game-over') {
      return;
    }
    
    let image = this.canvas.toDataURL('image/jpeg', 0.1);
    let body = JSON.stringify({
      image: image,
      id: localStorage.getItem('id') || ''
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    this.pointsAwarded = false;
    this.uploading = true;

    setTimeout(() => {
      componentHandler.upgradeDom();
    }, 0);

    this.http.post(this.uploadUrl, body, options)
      .toPromise()
      .then(res => {
        let data = res.json();
        this.uploading = false;

        if (data.success) {
          this.imageUploaded = true;
          localStorage.setItem('id', data.id);

          if (!this.gameService.selfieTaken) {
            // give the user some points for uploading their player id
            this.gameService.incrementPlayerScore(this.scoreIncrement);
            this.gameService.setSelfieTaken(true);
            this.pointsAwarded = true;
          }
        }
      })
      .catch(err => {
        this.uploading = false;
        this.imageUploaded = true;
        this.error = true;
      });
  }

  tryAgain() {
    this.canUpload = false;
    this.imageUploaded = false;
    this.error = false;
    this.uploading = false;
  }
}
