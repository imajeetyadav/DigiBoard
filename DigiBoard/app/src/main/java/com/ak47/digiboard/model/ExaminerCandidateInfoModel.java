package com.ak47.digiboard.model;

public class ExaminerCandidateInfoModel {

    private String name, email, profilePic;

    public ExaminerCandidateInfoModel() {

    }

    public ExaminerCandidateInfoModel(String name, String email, String profilePic) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
