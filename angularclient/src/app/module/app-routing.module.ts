import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {HomepageTitleComponent} from "../component/homepage-title/homepage-title.component";
import {AdminPageComponent} from "../component/admin-page/admin-page.component";


const routes: Routes = [
  {path: '', component: HomepageTitleComponent},
  {path: 'admin', component: AdminPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
