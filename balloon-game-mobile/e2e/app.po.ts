export class RhKeynoteDemoPage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('rh-keynote-demo-app h1')).getText();
  }
}
