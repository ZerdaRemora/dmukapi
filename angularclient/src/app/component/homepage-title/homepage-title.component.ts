import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'homepage-title',
  templateUrl: './homepage-title.component.html',
  styleUrls: ['./homepage-title.component.scss']
})
export class HomepageTitleComponent implements OnInit {
  title = 'homepage-title';
  quizTitle = 'Quiz';

  constructor() {}

  ngOnInit(): void {
  }

}
