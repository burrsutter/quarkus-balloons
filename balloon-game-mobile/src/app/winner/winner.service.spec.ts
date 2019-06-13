import {
  beforeEachProviders,
  it,
  describe,
  expect,
  inject
} from '@angular/core/testing';
import { WinnerService } from './winner.service';

describe('Winner Service', () => {
  beforeEachProviders(() => [WinnerService]);

  it('should ...',
      inject([WinnerService], (service: WinnerService) => {
    expect(service).toBeTruthy();
  }));
});
