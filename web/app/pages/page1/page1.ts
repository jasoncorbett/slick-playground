import {Page} from 'ionic-angular';


@Page({
  templateUrl: 'build/pages/page1/page1.html',
})
export class Page1 {
  counter: number;
  eb: any;
  
  constructor() {
    var that = this;
    this.counter = 0;
    this.eb = new EventBus(window.location.protocol + '//' + window.location.hostname + ':' + 9000 + '/eventbus');
    var eb = this.eb;
    this.eb.onopen = function () {
      eb.registerHandler('counter.changed', function(error, message) {
        that.counter = message.body.counter;
      });
    }
  }

  increment() {
    this.eb.send('counter.increment', {});
  }
}
