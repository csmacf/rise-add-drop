package dev.csmacf.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.csmacf.model.Student;
import dev.csmacf.service.CsvImportService;
import dev.csmacf.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;
    private final CsvImportService csvImportService;

    public StudentController(StudentService studentService, CsvImportService csvImportService) {
        this.studentService = studentService;
        this.csvImportService = csvImportService;
    }

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Integer id, Model model) {
        Optional<Student> studentOptional = studentService.findBySchoolId(id);
        if (studentOptional.isPresent()) {
            model.addAttribute("student", studentOptional.get());
            return "students/view";
        } else {
            model.addAttribute("error", "Student not found");
            return "redirect:/students";
        }
    }
    @GetMapping("/search")
    public String searchStudentsByEmail(@RequestParam("email") String email, Model model) {
        List<Student> students = studentService.findStudentsByEmailStartingWith(email);
        model.addAttribute("students", students);
        return "students/list";
    }
    @PostMapping("/upload")
    public String uploadStudentsCsv(@RequestParam("file") MultipartFile file, 
                                   RedirectAttributes redirectAttributes) {
        try {
            if (!file.getOriginalFilename().endsWith(".csv")) {
                redirectAttributes.addFlashAttribute("error", "Please upload a CSV file");
                return "redirect:/students";
            }

            List<String> results = csvImportService.processStudentsCsv(file);
            redirectAttributes.addFlashAttribute("uploadResults", results);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing file: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/template/download")
public void downloadTemplate(HttpServletResponse response) throws IOException {
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=students_template.csv");
    
    try (PrintWriter writer = response.getWriter()) {
        writer.write("SchoolID,Name,Email,AMCourseName,PMCourseName,AllDayCourseName\n");
        writer.write("12345,John Doe,john@cvsdvt.org,Math 101,Science 101,\n");
        writer.write("12346,Jane Smith,jane@cvsdvt.org,,,Full Day Course");
    }
}
    
}
