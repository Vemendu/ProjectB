package com.example.ProjectB.controllers;

import com.example.ProjectB.Entities.Client;
import com.example.ProjectB.helpers.TokenHelper;
import com.example.ProjectB.models.ClientRequest;
import com.example.ProjectB.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class ClientController {
    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new Client());

        return "signup_form";
    }

    @GetMapping("")
    public String viewHomePage()
    {
        return "index";
    }
    @PostMapping("/create") public ResponseEntity create(@RequestBody ClientRequest clientRequest) {
        boolean result = clientService.saveClient(clientRequest);
        if (result) {
            return new ResponseEntity("Client created", HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().body("Creation failed");
    }

    @PostMapping("/sign-in") public ResponseEntity signIn(@RequestBody ClientRequest clientRequest) {
        Client client = clientService.getClientByUsername(clientRequest.getUsername());
        if (client != null && client.getPassword().equals(clientRequest.getPassword())) {
                return new ResponseEntity(HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("Account doesn't exist");
    }

    @PostMapping("/log-out") public ResponseEntity logOut(HttpServletRequest request) {
        if(true)
            return new ResponseEntity("Logged  successfully", HttpStatus.OK);
        return ResponseEntity.badRequest().body("Bad request or account doesn't exist");
    }

    @GetMapping("/get") public ResponseEntity check(HttpServletRequest request) {
        /*String username = TokenHelper.getUsernameByToken(token);
        if (clientService.getClientByUsername(username)!=null) {
            Client client = clientService.getClientByUsername(username);
            return ResponseEntity.ok(client);
        }
        return ResponseEntity.badRequest().body("Invalid token");*/
        return ResponseEntity.badRequest().body("Invalid token");
    }

    @PostMapping("/update") public ResponseEntity update(@RequestBody ClientRequest clientRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        for(String bToken : clientService.getBlacklistedTokens())
        {
            if(token.equals(bToken))
                return ResponseEntity.badRequest().body("Token expired");
        }
        String username = TokenHelper.getUsernameByToken(token);
        boolean result = clientService.editClient(clientRequest);
        if (result) {
            return new ResponseEntity("Account updated", HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("Bad request upon updating");
    }

    @PostMapping("/delete") public ResponseEntity delete(HttpServletRequest request) {
        String token = request.getHeader("token");
        for(String bToken : clientService.getBlacklistedTokens())
        {
            if(token.equals(bToken))
                return ResponseEntity.badRequest().body("Token has already expired");
        }
        String username = TokenHelper.getUsernameByToken(token);
        boolean result1 = clientService.deleteClient(username);
        boolean result2 = clientService.addBlacklistedToken(request.getHeader("token"));
        if (result1 && result2) {
            return new ResponseEntity("Account deleted successfully", HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("Bad request upon deletion \n" +
                "Your token has already expired or your account is already deleted.");

    }
}
