# Knuckle_Down
Knuckle Down is a list-based ToDoApp with a Pomodoro-timer. [Find out more](#App_description) or [see screenshots](#Screenshots).

## Built with
- MVVM pattern
- Coroutines
- Flow
- LiveData (Transformations)
- ViewModel
- ListAdapter
- Room
- RecyclerView
- Navigation Component Library 
- Material Design

## App description <a name="App_description"></a> 
Now Knuckle Down contains five lists for tasks in the side menu:
- Inbox for for quickly adding a task or a task without time;
- Today displays the tasks for the current day;
- Tomorrow contains the tasks for the next day;
- Calendar allows you to display tasks for any specified day;
- Actions contain delegated tasks or tasks for which additional information is expected.
  
Using the Pomodoro timer implies that the user shouldn't use the phone when the timer is running. So the timer resets when configuration or state changes. 
<br />
Completed and overdue tasks are marked green and red, respectively.
<br />
The app supports a dark theme.
<br />
**The app has been tested by Talkback and Accessibility Scanner, so it is available for visually impaired or blind users.**

## Screenshots <a name="Screenshots"></a> 
The app has been tested on various devices:
- Physical devices: 
  - Yandex Phone (API 28, 5.65", 1080x2160)
  - Huawei Y5 2019 (API 28, 5.71", 720x1520)
- Virtual devices:
  - Pixel 2 (API 24, 5.0", 1080x1920)
  - Pixel XL (API 30, 5.5", 1440x2560)
  - Pixel 5 (API 30, 6.0", 1080x2320)
  - Pixel 6 Pro (API 32, 6.7", 1440x3120)
  
Below are screenshots from these devices. You can hover over the image to see which device it is. Dark theme enabled on some devices.
<br />
<p>
<img src="./screenshots/ya_today_list.png" title="Yandex Phone" width="250" vspace="30" />
<img src="./screenshots/ya_calend.png" title="Yandex Phone" width="250" vspace="30" hspace="30" />
</p>
<img src="./screenshots/ya_calend_land_dark.png" title="Yandex Phone" height="220" vspace="30" hspace="30" />
<p>
<img src="./screenshots/2_detail.png" title="Pixel 2" width="250" vspace="30" />
<img src="./screenshots/6pro_detail_dark.png" title="Pixel 6 Pro" width="250" vspace="30" hspace="30" />
</p>
<p>
<img src="./screenshots/5_calend_list_dark.png" title="Pixel 5" width="250" vspace="30" />
<img src="./screenshots/huawei_side.jpg" title="Huawei Y5 2019" width="250" vspace="30" hspace="30" />
</p>
<img src="./screenshots/5_side_land_dark.png" title="Pixel 5" height="220" vspace="30" hspace="30" />
<p>
<img src="./screenshots/huawei_inbox.jpg" title="Huawei Y5 2019" width="250" vspace="30" />
<img src="./screenshots/xl_timer.png" title="Pixel XL" width="250" vspace="30" hspace="30" />
</p>
<img src="./screenshots/xl_timer_land_dark.png" title="Pixel XL" height="220" vspace="30" />
<img src="./screenshots/6pro_timer_land_dark.png" title="Pixel 6 Pro" height="220" vspace="30" />
