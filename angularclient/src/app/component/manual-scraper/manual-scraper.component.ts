import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'manual-scraper',
  templateUrl: './manual-scraper.component.html',
  styleUrls: ['./manual-scraper.component.scss']
})
export class ManualScraperComponent {

  constructor(private http: HttpClient, private _snackBar: MatSnackBar) {}

  endPointTypes = new Map([
    ['bbc', 'BBC News'],
    ['dailymail', 'Daily Mail'],
    ['ukpolitics', 'r/UKPolitics']
  ]);

  sortTypes = new Map([
    ['highestrated', 'Highest Rated'],
    ['lowestrated', 'Lowest Rated'],
    ['created', 'Date Created']
  ]);

  sortOrders = new Map([
    ['descending', 'Descending'],
    ['acending', 'Ascending']
  ]);

  fetchedArticles = new Map([
    ['/', 'No recent articles with comments could be found']
  ]);

  title = 'endpointtester';
  endpointUrl;
  customEndpointUrl;
  sortType = 'highestrated';
  sortOrder = 'descending';
  useCustomCB = false;
  endpointType = '';
  articleFound: boolean = false;
  listRetrieved: boolean = false;


  grabArticles() {
    this.http.get("/api/headlines?src=" + this.endpointType).subscribe(
      (response) => this.responseReceived(response),
      (error) => console.log(error));
  }

  onCmtSubmit() {
    let url: string;
    if (this.useCustomCB) {
     url = this.customEndpointUrl;
    } else {
      url = this.endpointUrl;
    }

    this.http.get("/api/comments?url=" + url + "&sorttype=" + this.sortType + '&sortorder=' + this.sortOrder)
      .subscribe((response) => {
        this.resetVariables();
        this._snackBar.open("Comments added successfully", "close", {duration: 3000,});
        }, (error) => console.log(error));
  }

  checkCustomURL() {
    this.http.get("/api/articlecontainscomments?url=" + this.customEndpointUrl)
      .subscribe((response) => this.confirmCustomURL(response), (error) => console.log(error));
  }

  confirmCustomURL(response) {
    this.articleFound = response.payload;
  }

  responseReceived(response) {
    if (response != null) {
      this.listRetrieved = true;
      this.fetchedArticles = new Map(Object.entries(response.payload));

      if (this.fetchedArticles.size == 0) {
        this.fetchedArticles = new Map([
          ['/', 'No recent articles with comments could be found']
        ]);
      }
    }
    // TODO error on a null response?
  }

  safeToProceed() {
    return (this.articleFound || (!this.fetchedArticles.has('/') && this.endpointUrl != ''));
  }

  resetVariables() {
    this.articleFound = false;
    this.listRetrieved = false;
    this.endpointUrl = '';
    this.customEndpointUrl = '';
    this.endpointType = '';
    this.fetchedArticles = new Map([
      ['/', 'No recent articles with comments could be found']
    ]);
  }
}
