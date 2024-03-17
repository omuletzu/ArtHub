## ArtHub
  ArtHub is a gateway to the world of art for every person that is passioned by art.
  
<img src="https://github.com/omuletzu/ArtHub/assets/75565975/2394eeee-a060-4cfe-a7cb-9a2b95096da1" alt="app_logo" width="300">

## Features

- **Explore the community**: This is a place where everyone can share their art, so you can see what others did. Also, you can react by liking, saving or even seeing details of that post. There is a search feature that is based on tags. 
- **Add your art**: Display your artwork and don't forget to add a description, tags and even details, just in case someone is interested in what you did. You can choose by uploading a photo, sketch or a painting.
- **Customize your profile**: You have your own profile where everyone can see details about you, like nickname, avatar and contact informations. Here you can see your gallery with all your uploaded posts and even saved and liked ones.
- **Report**: There is a report feature, just in case a post or a user is bad for community.

## Implementation overview

- **Technologies used**: this app was made in **Android Studio** and it uses **Kotlin** for frontend and **XML** for backend. Also it has **WordNET**, a lexical database and it is manipulated by **JWI** library (this is used for search by tags feature)
- **Data Storage and Management**: user data, artwork information and other relevant data are stored in **Firebase**, a database hosted by Google.
- **User authentication and authorization**: this is handled by **Firebase Authentication**. It requires each user to input an email and a strong password, in order to create an account.

## Installation 

  In order to install ArtHub on your phone, you need to get all the files on your machine, that has **Android Studio**, because using this tool, you can build the APK.

- **Step1**: Download all the files, either manually, or in your git bash use `git clone https://github.com/omuletzu/ArtHub`
- **Step2**: You have to open all this files as a project in **Android Studio** and build the APK, and after this, using an USB cable get the APK on your phone and run the APK

## USAGE 

- **Sign in or create an account**: First of all you must login into your account or create one. For the registering step, if you don't want to use your real information just type random data.
- **Explore**: After entering you get to the **explore** page. Here you can either search by tags or explore random posts, or go to specific categories (painting, sketch, photo).
- **Add**: Choose between one of the 3 types, write a good description, add tags and details and hit **Next**, check all the details to be all right, then hit **Post**.
- **Your profile**: Here you can either see your gallery (by clicking **Gallery**), or go through **Settings** where you can change your **personal information**, change to **Dark Mode**, **block** other users or sign out. Also here you can change the **avatar** or **nickname**.
