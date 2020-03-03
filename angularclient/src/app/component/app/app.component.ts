import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ThemingService} from "../theming-service/theming-service.service";
import {Observable} from "rxjs";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  darkTheme: Observable<boolean>;
  darkToggle: boolean;

  public constructor(private titleService: Title, private themingService: ThemingService, private cookieService: CookieService) {}

  public setTitle(title: string) {
    this.titleService.setTitle(title);
  }

  ngOnInit() {
    this.setTitle('DMUK');
    this.darkTheme = this.themingService.isDarkTheme;

    // If cookie doesn't already exist, default to false.
    if (!this.cookieService.check("darkmode"))
      this.cookieService.set("darkmode", "false");

    let cookieVal = this.cookieService.get("darkmode");

    this.darkToggle = Boolean(JSON.parse(cookieVal));

    // This is needed in order to flip to the dark theme (if necessary)
    // AFTER the page has finished loading.
    setTimeout(() => {
      this.toggleDarkTheme(this.darkToggle);
    });
  }

  toggleDarkTheme(enabled: boolean) {
    this.cookieService.set("darkmode", String(enabled));
    this.themingService.setDarkTheme(enabled);
  }

}
