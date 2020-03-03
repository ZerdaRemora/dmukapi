import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { RandomCommentService } from "./random-comment-service";
import { Comment } from "../../model/Comment";

@Component({
  selector: 'random-comment-quiz',
  templateUrl: './random-comment-quiz.html',
  styleUrls: ['./random-comment-quiz.scss'],
  providers: [RandomCommentService]
})
export class RandomCommentQuiz implements OnInit {

  @Output() exitQuizEmitter = new EventEmitter<string>();

  currentCmt: Comment;
  displayBBC = false;
  displayDM = false;
  displayUKP = false;
  beenAnswered = false;
  wasCorrect = false;
  currentScore = 0;

  constructor(private http: HttpClient, private randomCommentService: RandomCommentService ) {}

  ngOnInit(){
    this.grabCurrentSources();
    this.nextComment();
  }

  exitQuiz()
  {
    this.exitQuizEmitter.next( String(this.currentScore));
  }


  grabCurrentSources(){
    this.http.get("/quiz/getsources")
      .subscribe((response) => this.processSources(response), (error) => console.log(error));
  }

  processSources(response)
  {
    if (response.statusCode == "OK") {
      let sourcesMap = new Map(Object.entries(response.payload));
      this.displayBBC = sourcesMap.get('bbc');
      this.displayDM = sourcesMap.get('dailymail');
      this.displayUKP = sourcesMap.get('ukpolitics');
    }
  }

  nextComment()
  {
    // TODO Check if last comment, then exit

    this.randomCommentService.getRandomCmt()
      .subscribe((comment: Comment) => this.processNextComment(comment));
    this.beenAnswered = false;
    this.wasCorrect = false;
  }

  processNextComment(response){
    if (response.statusCode == "OK" && response.payload != null) {
      let comment = response.payload;

      this.currentCmt = {
        id: comment['id'],
        author: comment['author'],
        body: comment['body'],
        commentSource: comment['commentSource'],
        articleTitle: comment['articleTitle'],
        score: comment['score'],
        date: comment['date']
      };

      this.beenAnswered = false;
      this.wasCorrect = false;

    } else {
      this.exitQuiz();
    }
  }

  safeToDisplay()
  {
    return this.currentCmt != null;
  }

  guessDM()
  {
    this.guessProcessor("DAILYMAIL")
  }

  guessBBC()
  {
    this.guessProcessor("BBC");
  }

  guessUKP()
  {
    this.guessProcessor("R_UKPOLITICS");
  }

  guessProcessor(source){
    this.beenAnswered = true;
    if (this.currentCmt.commentSource == source){
      this.wasCorrect = true;
      this.currentScore += 1;
    } else {
      this.wasCorrect = false;
    }
  }

}
