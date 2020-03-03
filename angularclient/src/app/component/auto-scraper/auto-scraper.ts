import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { AutoScraperChecks } from "../../model/AutoScraperChecks";

@Component({
  selector: 'auto-scraper',
  templateUrl: './auto-scraper.html',
  styleUrls: ['./auto-scraper.scss']
})
export class AutoScraper {


  autoScraperChecks: AutoScraperChecks = new AutoScraperChecks();

  constructor(private http: HttpClient) {
    this.autoScraperChecks.autoBBCChecked = false;
    this.autoScraperChecks.autoDailyMailChecked = false;
    this.autoScraperChecks.autoUKPoliticsChecked = false;
    this.autoScraperChecks.sourceError = false;
  }

  onSubmit() {
    if (this.autoScraperChecks.autoBBCChecked || this.autoScraperChecks.autoDailyMailChecked || this.autoScraperChecks.autoUKPoliticsChecked)
    {
      this.autoScraperChecks.sourceError = false;
      this.http.post("/api/triggerautofetch", {
        useBBC: this.autoScraperChecks.autoBBCChecked,
        useDM: this.autoScraperChecks.autoDailyMailChecked,
        useUKP: this.autoScraperChecks.autoUKPoliticsChecked
      })
        .subscribe((response) => console.log(response), (error) => console.log(error));
    } else {
      this.autoScraperChecks.sourceError = true;
    }
  }

}
