package com.example.demo.controller;

import com.example.demo.model.Leave;
import com.example.demo.model.User;
import com.example.demo.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        User user = leaveService.findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            if (user.getRole().equals("MANAGER")) {
                return "redirect:/manager/dashboard";
            } else {
                return "redirect:/employee/dashboard";
            }
        }
        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        leaveService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        List<Leave> leaves = leaveService.getLeavesByUser(user);
        model.addAttribute("leaves", leaves);
        model.addAttribute("user", user);
        return "employee-dashboard";
    }

    @PostMapping("/employee/apply")
    public String applyLeave(@RequestParam String reason,
                             @RequestParam String fromDate,
                             @RequestParam String toDate,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Leave leave = new Leave();
        leave.setReason(reason);
        leave.setFromDate(java.time.LocalDate.parse(fromDate));
        leave.setToDate(java.time.LocalDate.parse(toDate));
        leave.setUser(user);
        leaveService.applyLeave(leave);
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/manager/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (!user.getRole().equals("MANAGER")) return "redirect:/login";
        List<Leave> pending = leaveService.getPendingLeaves();
        List<Leave> all = leaveService.getAllLeaves();
        model.addAttribute("pendingLeaves", pending);
        model.addAttribute("allLeaves", all);
        model.addAttribute("user", user);
        return "manager-dashboard";
    }

    @PostMapping("/manager/update")
    public String updateLeave(@RequestParam int leaveId,
                              @RequestParam String status,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        leaveService.updateLeaveStatus(leaveId, status);
        return "redirect:/manager/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}