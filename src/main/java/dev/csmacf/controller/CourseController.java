package dev.csmacf.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.csmacf.model.Course;
import dev.csmacf.model.Student;
import dev.csmacf.service.CourseService;
import dev.csmacf.service.CsvImportService;
import dev.csmacf.service.TeacherService;
import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CsvImportService csvImportService;
    private final TeacherService teacherService;
    

public CourseController(CourseService courseService, CsvImportService csvImportService, TeacherService teacherService) {
    this.courseService = courseService;
    this.csvImportService = csvImportService;
    this.teacherService = teacherService;

}
    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        if (courses != null) {
            model.addAttribute("courses", courses);
            return "courses/list";
        } else {
            model.addAttribute("error", "Course not found");
            return "redirect:/courses";
        }
        
    }

    @GetMapping("/view/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "courses/view";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("allTeachers", teacherService.getAllTeachers());
        return "courses/form";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("allTeachers", teacherService.getAllTeachers());
        return "courses/form";
    }

    @PostMapping
    public String saveCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {
        try {
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("message", 
                "Course " + course.getName() + " saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }
    @GetMapping("/export/{id}")
    @ResponseBody
    public void exportRoster(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Course course = courseService.getCourseById(id);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=Roster" + course.getId() + ".csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.write("Name,Email,Grade,House\n");
            List<Student> students = course.getAllStudents();
            for (Student student : students) {
                writer.write(student.getName() + "," + student.getEmail() + "," + student.getGrade() + "," + student.getHouse() + "\n");
            }
        }
        catch (IOException e) {
            response.getWriter().write("Error exporting roster: " + e.getMessage());
        }
    }
    

   

    @PostMapping("/upload")
    public String uploadCoursesCsv(@RequestParam("file") MultipartFile file, 
                                  RedirectAttributes redirectAttributes) {
        try {
            if (!file.getOriginalFilename().endsWith(".csv")) {
                redirectAttributes.addFlashAttribute("error", "Please upload a CSV file");
                return "redirect:/courses";
            }

     
                        List<String> results = csvImportService .processCoursesCsv(file);
            redirectAttributes.addFlashAttribute("uploadResults", results);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing file: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/template/download")
public void downloadTemplate(HttpServletResponse response) throws IOException {
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=courses_template.csv");
    
    try (PrintWriter writer = response.getWriter()) {
        writer.write("Name,Room,Description,ScheduleType,Capacity,Teacher1Email,Teacher2Email,Teacher3Email,Teacher4Email\n");
        writer.write("Knitting,101,Basic knitting course,AM,30,teacher1@cvsdvt.org,teacher2@cvsdvt.org,,\n");
        writer.write("Crosswords,Library,Introductory crosswords course,PM,25,teacher3@cvsdvt.org,,,");
    }
}
}