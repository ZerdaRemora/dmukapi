import {Component, OnInit} from '@angular/core';
import {HealthCheckService} from "./health-check.service";

@Component({
  selector: 'health-check',
  templateUrl: './health-check.component.html',
  styleUrls: ['./health-check.component.scss'],
  providers: [HealthCheckService]
})
export class HealthCheckComponent implements OnInit {
  title = 'top-comment';
  health: Boolean = false;

  constructor(private healthCheckService: HealthCheckService) {}

  ngOnInit() {
    this.healthCheckService.getHealth().subscribe(data => {
        if (data['status'] == "UP")
          this.health = true;
      },
      error => {
        if (!error['ok'])
          console.log("bad")
      })
  };

}
