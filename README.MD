# Wisdom

This app was designed to be a replacement for the Williamsville Information Tracking System (WITS),
where it would provide information such as school news, grades, and an internal emailing system.
The latest revision to their website (in 2015) had made the mobile experience slow, confusing
(there were two drop down options at the top of the page, with no indication of which accessed which
menu), and in some cases (such as my own phone) would freeze the entire browser.

This project was a pet project, with a few goals in mind. First, provide a better, native
experience to the website. Second, learn Android development, as I had no previous experience.

Unfortunately, this project was shut down right midway during development. However, they did not
request the code be removed, so I have placed this on Github in case I need to refer back to this.

Please pardon the lack of documentation. This was written before I had developed better coding
practices.

## Functionality

This application

## Development information

This application was developed in around 8 weeks, from scratch, using Android Studio 1.3.1. This
includes time learning Android development.

This application attempted to follow Google's Material standard as much as possible.

This application, before stopping development, had the following features:

* Classes: Show currently enrolled classes and the currently held average for that class
* Grades: Showed the points gained / total points, and the average of that assignment. Also showed
the date the grade entry was posted.
* Recent grades: Showed the recent grades (within a certain time period) from various classes. This
was especially helpful if you wished to quickly know your latest test score.
* Mail: View mails sent through the internal mailing system. Replying to them was a planned feature.
* User specific settings: This app would automatically pull your yearbook picture and school theme
and automatically apply them to the app.
* School newsletter: This app had an integrated RSS feed from the school's newspaper club's website.

Planned features included a calender for upcoming events, and implementation of the internal forum
subsystem.

## Notes

### Lack of API

WITS did not have a API (public facing, at the very least). Instead, I developed a webscraper in PHP
to act as my API. An instance of this app would send a request to this server with the user's
credentials, and the server would return JSON data after scraping the necessary data from their
website. This was intended to be a temporary solution until they released a public API. The server
code will not be released, as a security precaution.

### Security vulnerabilities

During development, I had discovered two vulnerabilities to their system. The first allowed anyone
to view any email sent with their internal messaging system. This includes messages up to those sent
in 2007. The second was an XSS vector that allowed execution of arbitrary code. Both were reported
to the head engineer of the time as soon as they were discovered. The first was quickly fixed; the
latter would then be the primary mechanism to create the first proto-virus on their messaging
system, created by another student.

As of writing this (8/20/17), it is unknown whether or not the latter has been patched or not.

### Copyright

The Williamsville Information Tracking System is owned by the Williamsville Central School District.
Some portions of this app has code under their respective owners' license. The rest of this project
is public domain.