import { RouterOutlet } from '@angular/router';
import { Component, inject } from '@angular/core';
import { Header } from '../../layout/header/header';
import { Sidebar } from '../../layout/sidebar/sidebar';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideActivity, lucideArrowUp, lucideTicket } from '@ng-icons/lucide';
import { GlobalStore } from '../../store/global/global.store';

@Component({
  imports: [RouterOutlet, Header, Sidebar, NgIcon],
  providers: [GlobalStore, provideIcons({ lucideActivity, lucideArrowUp, lucideTicket })],
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class DashboardPage {
  readonly store = inject(GlobalStore);
  readonly title = 'Dashboard';
}
