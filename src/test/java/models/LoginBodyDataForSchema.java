package models;

import lombok.Data;


@Data
public class LoginBodyDataForSchema {
    String id = "data.id",
            email = "data.email",
            firstName = "data.first_name",
            lastName = "data.last_name";


}