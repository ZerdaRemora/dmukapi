import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {RandomCommentService} from "../random-comment-quiz/random-comment-service";
import {RandomCommentQuiz} from "../random-comment-quiz/random-comment-quiz";

@Component({
  selector: 'quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.scss']
})
export class QuizComponent implements OnInit {

  constructor(private http: HttpClient) {}

  dailyMailChecked = false;
  uKPoliticsChecked = false;
  bbcChecked = false;
  commentsToGuess = 5;
  isFormValid = false;
  quizInProgress = false;
  loadingComments = false;

  ngOnInit() {
  }

  checkValidity()
  {
    let isValid = true;
    let errorMsg = '';

    if (! ((this.dailyMailChecked && this.bbcChecked) || (this.dailyMailChecked && this.uKPoliticsChecked) || (this.bbcChecked && this.uKPoliticsChecked)))
    {
        isValid = false;
        errorMsg = ("- Must select at least 2 comment sources");
    }
    if (this.commentsToGuess >= 100)
    {
        isValid = false;
        if (errorMsg != ''){
          errorMsg += "\n";
        }
        errorMsg += ("- Current maximum number quiz comments is 100");
    }

    if (isValid)
    {
      this.isFormValid = true;
      document.getElementById("submit").title = '';
    } else {
      this.isFormValid = false;
      document.getElementById("submit").title = errorMsg;
    }
  }

  quizSubmit()
  {
    this.loadingComments = true;
    let formData: FormData = new FormData();
    formData.append('usebbc', String(this.bbcChecked));
    formData.append('usedm', String(this.dailyMailChecked));
    formData.append('useukp', String(this.uKPoliticsChecked));
    formData.append('cmtcount', String(this.commentsToGuess));
    this.http.post("/quiz/startquiz", formData)
      .subscribe((response) => this.startQuiz(response), (error) => console.log(error));
  }

  startQuiz(response)
  {
    // TODO generate error if not enough comments exist in the DB

    let canContinue = Boolean(response.payload);

    if (canContinue) {
      this.quizInProgress = canContinue;
      let newQuiz = new RandomCommentQuiz(this.http, new RandomCommentService(this.http));
    }

    this.loadingComments = false;
  }

  quizComplete($event)
  {
    // $event is current the score of correct answers passed back
    // Currently unsure what to do with the score, but its nice to have it now
    // console.log($event); // <- Uncomment to print returned score
    this.quizInProgress = false;
  }

}
