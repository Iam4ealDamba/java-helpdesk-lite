import { Component } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { featherMail, featherSearch } from '@ng-icons/feather-icons';
import { phosphorNotification } from '@ng-icons/phosphor-icons/regular';

@Component({
  imports: [NgIcon],
  providers: [provideIcons({ featherMail, phosphorNotification, featherSearch })],
  selector: 'app-header-layout',
  styleUrl: './header.css',
  templateUrl: './header.html',
})
export class Header {}
