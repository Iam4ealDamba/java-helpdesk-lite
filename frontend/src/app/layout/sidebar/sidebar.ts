import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideChevronDownCircle,
  lucideLayoutDashboard,
  lucideBell,
  lucideUsers,
  lucideTicket,
  lucideSettings,
} from '@ng-icons/lucide';
import { remixSearchLine } from '@ng-icons/remixicon';
import { GlobalStore } from '../../store/global/global.store';
import { NgClass } from '../../../../node_modules/@angular/common/types/_common_module-chunk';

@Component({
  imports: [NgIcon, FormsModule, NgClass],
  providers: [
    GlobalStore,
    provideIcons({
      lucideChevronDownCircle,
      remixSearchLine,
      lucideLayoutDashboard,
      lucideBell,
      lucideUsers,
      lucideTicket,
      lucideSettings,
    }),
  ],
  selector: 'app-sidebar-layout',
  styleUrl: './sidebar.css',
  templateUrl: './sidebar.html',
})
export class Sidebar {
  readonly store = inject(GlobalStore);
  readonly navList = [
    {
      id: 'homepage',
      title: 'Dashboard',
      isActive: false,
    },
    {
      id: 'tickets',
      title: 'Tickets',
      isActive: false,
    },
    {
      id: 'members',
      title: 'Members',
      isActive: false,
    },
    {
      id: 'notifications',
      title: 'Notifications',
      isActive: false,
    },
    {
      id: 'settings',
      title: 'Parametres',
      isActive: false,
    },
  ];
  globalSearchControl = signal('');
}
