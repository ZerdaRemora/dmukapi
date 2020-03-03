import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable()
export class HealthCheckService {

  constructor(private http: HttpClient) {}

  getHealth() {
    return this.http.get("/actuator/health");
  }
}

