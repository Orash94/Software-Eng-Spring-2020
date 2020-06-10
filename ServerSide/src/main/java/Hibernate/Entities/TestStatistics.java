package Hibernate.Entities;

import javax.persistence.*;

@Entity
@Table(name = "Test_Statistics")
public class TestStatistics {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "testStatisticsId")
	private int id;
	
	@OneToOne(mappedBy = "statistics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Test test;
	
	@Column(name = "numberOfStudentsInTest")
	private int numberOfStudentsInTest;
	
	@Column(name = "numberOfStudentsThatFinishedInTime")
	private int numberOfStudentsThatFinishedInTime;
	
	@Column(name = "averageGrade")
	private double averageGrade;
	
	public TestStatistics() {
	}

	public int getId() {
		return id;
	}
	
	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public int getNumberOfStudentsInTest() {
		return numberOfStudentsInTest;
	}

	public void increaseNumberOfStudentsInTest() {
		this.numberOfStudentsInTest++;
	}

	public int getNumberOfStudentsThatFinishedInTime() {
		return numberOfStudentsThatFinishedInTime;
	}

	public void increaseNumberOfStudentsThatFinishedInTime() {
		this.numberOfStudentsThatFinishedInTime++;
	}

	public double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}


	
	
	
	
	
}
