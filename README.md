Expense Tracker App

Overview

The Expense Tracker App is a simple application built using Jetpack Compose and Kotlin that allows users to track their expenses, view monthly and yearly summaries, and download an expense report.

Features

Expense Summary: Displays a categorized summary of expenses.

Monthly & Yearly Tabs: Users can switch between monthly and yearly expenses.

Expense Chart: Visual representation of expenses.

Download Report: Generates and downloads an expense report.

Permission Handling: Requests necessary permissions for storage access.

https://github.com/user-attachments/assets/b547d30b-52ee-4c89-a1c1-3769158729bd



Scrollable Content: Ensures the UI remains user-friendly even with large amounts of data.

Technologies Used

Kotlin: Programming language

Jetpack Compose: UI toolkit for modern Android UI development

ViewModel: Manages UI-related data

State Management: Uses collectAsState to manage data

Permissions API: Handles storage permissions dynamically

LazyColumn: Enables scrolling for large datasets

How It Works

Expense Data Fetching

Retrieves expenses from the MainViewModel based on the selected tab (monthly/yearly).

Displaying Expenses

If no expenses exist, a placeholder image and message appear.

Otherwise, a chart and list of expense categories are displayed.

Downloading Report

Clicking the "Download Expense Report" button checks the Android version.

If the Android version is below Android 10 (Q), it requests WRITE_EXTERNAL_STORAGE permission.

If granted, it generates and saves the expense report.

Installation

Open the project in Android Studio.

Run the project on an emulator or a physical device.

Permissions

The app requests the following permissions:

WRITE_EXTERNAL_STORAGE (For Android versions below 10)

UI Improvements

Fixed Button Position: The "Download Expense Report" button remains visible even when scrolling.

Smooth Scrolling: Expenses are displayed in a LazyColumn to ensure performance efficiency.

Future Enhancements

Add a database for expense persistence.

Implement data export in PDF format.

Include a search/filter feature for expense categories.

License

This project is open-source and available under the MIT License.
