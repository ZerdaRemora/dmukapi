import {Component, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Comment} from "../../model/Comment";
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator, PageEvent} from '@angular/material/paginator';

@Component({
  selector: 'admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  title = 'admin-page';

  displayedColumns: string[] = ['id', 'author', 'body', 'score', 'articleTitle', 'commentSource', 'date'];
  dataSource: MatTableDataSource<Comment>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  totalRecords = 0;
  pageSize = 10;
  pageIndex = 0;

  constructor(private http: HttpClient) {
    this.dataSource = new MatTableDataSource<Comment>([]);
  }

  ngOnInit(): void {
    this.loadComments();
  }

  loadComments(): void {
    const params = {
      draw: 1,
      start: this.pageIndex * this.pageSize,
      length: this.pageSize,
      search: {value: '', regex: false},
      order: [{column: 0, dir: 'asc'}],
      columns: this.displayedColumns.map((col, i) => ({
        data: col,
        name: '',
        searchable: false,
        orderable: false,
        search: {value: '', regex: false}
      }))
    };

    this.http.post<any>("/api/filtercomments", params)
      .subscribe((response) => {
        this.dataSource.data = response.data;
        this.totalRecords = response.recordsTotal;
      }, (error) => console.log(error));
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadComments();
  }
}
