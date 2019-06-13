import {
  beforeEachProviders,
  describe,
  expect,
  it,
  inject
} from '@angular/core/testing';
import { RhKeynoteDemoAppComponent } from '../app/rh-keynote-demo.component';

beforeEachProviders(() => [RhKeynoteDemoAppComponent]);

describe('App: RhKeynoteDemo', () => {
  it('should create the app',
      inject([RhKeynoteDemoAppComponent], (app: RhKeynoteDemoAppComponent) => {
    expect(app).toBeTruthy();
  }));

  it('should have as title \'rh-keynote-demo works!\'',
      inject([RhKeynoteDemoAppComponent], (app: RhKeynoteDemoAppComponent) => {
    expect(app.title).toEqual('rh-keynote-demo works!');
  }));
});
