import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { Comment } from '../../model/Comment';



@Injectable()
export class RandomCommentService {

  comment: Comment;

  constructor(private http: HttpClient) {}

  getRandomCmt() {
    return this.http.get("/quiz/nextcmt");
  }
}
