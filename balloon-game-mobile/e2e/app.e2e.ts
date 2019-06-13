import { RhKeynoteDemoPage } from './app.po';

describe('rh-keynote-demo App', function() {
  let page: RhKeynoteDemoPage;

  beforeEach(() => {
    page = new RhKeynoteDemoPage();
  })

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('rh-keynote-demo works!');
  });
});
