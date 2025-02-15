# 📅 Meetings-App-Android

A user-friendly Planner App designed to manage and organize meetings efficiently. The app provides an intuitive interface for adding, viewing, and deleting meetings, with a weekly view to track scheduled events. 

## ✨ Features

### 🏠 Home Page
- **➕ Add New Meetings:** Click the left button to add a new meeting.
- **📆 Weekly View:** Click the middle button to navigate to the weekly planner.
- **🗑️ Delete All Meetings:** Click the right button to remove all scheduled meetings.
- **⬅️➡️ Navigation:** Use the arrow buttons to scroll through the calendar. Selecting a date and clicking the middle button displays the meetings scheduled for that week.

### 📅 Weekly View
- **🔄 Scroll Through Dates:** Navigate through different days using the arrow buttons.
- **📋 View Meetings by Day:** Clicking on a date displays the list of meetings scheduled for that day.
- **⏩ Push Meetings:** The left button moves the selected meeting to the next weekday or weekend.
- **➕ Add Meetings:** The middle button allows users to add new meetings.
- **🗑️ Delete Meetings:** The right button removes meetings for the selected day.

### 📝 Add Meeting Screen
- **📌 Meeting Details:** Enter meeting title, date, and time.
- **📞 Contact List Integration:** Add participants from the phone's contact list.

## 🚀 Installation
1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/planner-app.git
   cd planner-app
   ```
2. **Open in Android Studio**
   - Open **Android Studio**.
   - Click **File > Open...** and select the project folder.
   - Allow the project to synchronize and install dependencies.

3. **🔧 Build and Run the App**
   - Navigate to **Build > Build APK(s)**.
   - Install the generated APK on an Android device or emulator.

## 🎯 Usage
- **➕ Adding a Meeting:** Click the add button, fill in details, and save.
- **📅 Viewing Meetings:** Navigate through the calendar and select a date.
- **🗑️ Deleting Meetings:** Remove specific meetings or clear all meetings using the delete button.

## 📂 Project Structure
```
PlannerApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/plannerapp/ 
│   │   │   │   ├── MainActivity.java  # Main activity handling UI and logic
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml  # Home Page UI
│   │   │   │   │   ├── activity_weekly.xml  # Weekly View UI
│   │   │   │   └── values/
│   │   │   │       └── strings.xml  # Application strings
│   │   │   └── AndroidManifest.xml  # App configuration
├── build.gradle  # Gradle build file
└── README.md  # Project documentation
```

## 🙌 Acknowledgements
This Planner App was developed to explore Android UI design, memory management, and fundamental scheduling functionalities.

## 📜 License
This project is licensed under the MIT License - see the LICENSE file for details.

### 📝 MIT License
```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

