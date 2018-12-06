package com.in28minutes.springboot.rest.example.student;

import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Generated;
import org.springframework.data.annotation.Id;

@Entity
public class Student {
		@Id
		@Generated
		private Long id;
		private String name;
		private String passportNumber;
		

		
}
