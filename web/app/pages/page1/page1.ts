import {Page} from 'ionic-angular';

declare var EventBus: any;

@Page({
  templateUrl: 'build/pages/page1/page1.html',
})
export class Page1 {
  projects: Array<any>;
  eb: any;
  
  constructor() {
    var that = this;
    this.projects = [];
    this.eb = new EventBus(window.location.protocol + '//' + window.location.hostname + ':' + 9000 + '/eventbus');
    var eb = this.eb;
    this.eb.onopen = function () {
      eb.registerHandler('slick.db.project.new', function(error, message) {
        that.projects.push(message.body);
      });
      eb.send('slick.db.query', {type: 'project', query: {}}, function(error, message) {
        that.projects = message.body;
      });
    }
  }

  projectAdded(error, message) {
    this.projects.push(message.body);
  }
}
