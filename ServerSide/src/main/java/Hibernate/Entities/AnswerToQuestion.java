package Hibernate.Entities;

import javax.persistence.*;

@Entity
@Table(name = "Answer_To_Question")
public class AnswerToQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AnswerToQuestionId")
	private int id;

	@Column(name = "questionId")
	private int questionId;
	
	@Column(name = "studentAnswer")
	private int studentAnswer;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "StudentTestId")
	private StudentTest student;

	public AnswerToQuestion() {
	}

	public AnswerToQuestion(int studentAnswer, StudentTest student, int questionId) {
		this.studentAnswer = studentAnswer;
		setStudent(student);
		this.questionId = questionId;
	}

	public int getId() {
		return id;
	}
	
	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getStudentAnswer() {
		return studentAnswer;
	}

	public void setStudentAnswer(int studentAnswer) {
		this.studentAnswer = studentAnswer;
	}

	public StudentTest getStudent() {
		return student;
	}

	public void setStudent(StudentTest student) {
		this.student = student;
		student.getAnswers().add(this);
	}	
}