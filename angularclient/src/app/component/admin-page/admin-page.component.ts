import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Comment} from "../../model/Comment";
import {APIResponse} from "../../model/APIResponse";
import {DataTableResponse} from "../../model/DataTableResponse";

@Component({
  selector: 'admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  title = 'admin-page';
  dtOptions: DataTables.Settings = {};
  commentList: Comment[];

  constructor(private http: HttpClient) {}

  ngOnInit(): void
  {
    this.dtOptions = {
      scrollX: true,
      pageLength: 10,
      serverSide: true,
      processing: true,
      searching: false,
      ajax: (params, callback) => {
        this.http.post<DataTableResponse>("/api/filtercomments", params)
          .subscribe((response) => {
            this.commentList = response.data;

            callback({
              recordsTotal: response.recordsTotal,
              recordsFiltered: response.recordsFiltered,
              data: []
            });


          }, (error) => console.log(error));
      },
      columns: [
        { data: 'id', width: '5%' },
        { data: 'author', width: '15%' },
        { data: 'body', width: '45%' },
        { data: 'score', width: '5%' },
        { data: 'articleTitle', width: '15%' },
        { data: 'commentSource', width: '5%' },
        { data: 'date', width: '10%' }
      ]
    };
  }

}
