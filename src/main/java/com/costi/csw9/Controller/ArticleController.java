package com.costi.csw9.Controller;
import com.costi.csw9.Model.Article;
import com.costi.csw9.Model.FlashMessage;
import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Service.ArticleService;
import com.costi.csw9.Service.RegistrationRequest;
import com.costi.csw9.Service.RegistrationService;
import com.costi.csw9.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private RegistrationService registrationService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService, RegistrationService registrationService) {
        this.articleService = articleService;
        this.userService = userService;
        this.registrationService = registrationService;
    }
    /*
    @GetMapping
    public List<Article> getarticles(){
        return articleService.getArticles();
    }

    @PostMapping
    public void registerNewArticle(@RequestBody Article article){
        articleService.addNewArticle(article);
    }

    @DeleteMapping(path = "{articleId}")
    public void deleteArticle(@PathVariable("articleId") Long articleId){
        articleService.deleteArticle(articleId);
    }

    @PutMapping(path = "{articleId}")
    public void modifyArticle(@PathVariable("articleId") Long articleId, @RequestParam(required = false) String name, @RequestParam(required = false) String email){
        articleService.modifyArticle(articleId, name, email);
    }*/
    //Account
    private User getCurrentUser(Principal principal) {
        if(principal == null){
            return new User("NULL","NULL","Not Signed In","error", UserRole.USER);
        }
        String username = principal.getName();
        User u = userService.loadUserObjectByUsername(username);
        return u;
    }

    @RequestMapping("/Account")
    public String editUser(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("action", "/Account/edit");
        model.addAttribute("loggedIn", principal != null);
        return "main/ViewAccount";
    }

    @PostMapping("/Account/edit")
    public String updateUser(User user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.User",result);
            // Add  member if invalid was received
            redirectAttributes.addFlashAttribute("user",user);
        }

        //Transfer id so it gets overwritten in data
        User currentUser = getCurrentUser(principal);
        user.setId(currentUser.getId());

        //Enable user if enabled
        user.setEnabled(currentUser.getEnabled());

        //Transfer role
        user.setRole(currentUser.getRole());

        //Save new user
        userService.updateUser(user);
        return "main/ViewAccount";
    }

    @GetMapping("/SignUp")
    public String getNewAccount(Model model, RedirectAttributes redirectAttributes) {
        if(!model.containsAttribute("user")) {
            model.addAttribute("user",new User());
        }
        model.addAttribute("action","/SignUp/post");
        return "main/NewAccount";
    }

    @RequestMapping(value = "/SignUp/post", method = RequestMethod.POST)
    public String addNewUser(User user, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);

            // Re populate credentials in form
            redirectAttributes.addFlashAttribute("user", user);

            // Redirect back to the form
            return "redirect:/SignUp";
        }
        if(user.getRole().name().equals("ADMIN")){
            registrationService.registerAdmin(user);
            return "main/Home";
        }
        registrationService.registerUser(user);
        return "main/Home";
    }

    //Main
    @GetMapping("/")
    public String getHome(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "main/Home";
    }
    @GetMapping("/Projects")
    public String getProjects(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "main/Projects";
    }
    @GetMapping("/login")
    public String getLogin(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "main/login";
    }

    //Wiki
    @GetMapping("/Wiki")
    public String getWikiHome(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "wiki/WikiHome";
    }

    //Minecraft
    @GetMapping("/Minecraft")
    public String getMCHome(Model model, Principal principal){
        model.addAttribute("user", getCurrentUser(principal));
        model.addAttribute("loggedIn", principal != null);
        return "minecraft/MCHome";
    }
    @GetMapping("/Minecraft/gov")
    public String getGovernmentInfo(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/yourGovernment";
    }
    @GetMapping("/Minecraft/vote")
    public String getVoting(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/votingCenter";
    }
    @GetMapping("/Minecraft/vote/VotingBooth")
    public String getVotingBooth(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/votingBooth";
    }
    @GetMapping("/Minecraft/vote/allCitizens")
    public String getAllCitizens(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/allCitizens";
    }
    @GetMapping("/Minecraft/vote/register")
    public String getRegister(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/register";
    }
    @GetMapping("/Minecraft/vote/runForOffice")
    public String getAddCandidate(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/addCandidate";
    }
    @GetMapping("/Minecraft/vote/Polls")
    public String getPolls(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/polls";
    }
    @GetMapping("/Minecraft/vote/BallotInfo")
    public String getBallotInfo(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/ballotInfo";
    }
    @GetMapping("/Minecraft/vote/results")
    public String getResults(Model model, Principal principal){
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("user", getCurrentUser(principal));
        return "minecraft/electionResults";
    }

    //Adding New Articles
    @RequestMapping("/Minecraft/Articles/Add")
    public String formNewMember(Model model, Principal principal) {
        if(!model.containsAttribute("article")) {
            model.addAttribute("article",new Article());
        }
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("action","/Articles/Upload");
        model.addAttribute("submit","Add");
        return "minecraft/uploadArticle";
    }

    @PostMapping("/Minecraft/Articles/Upload")
    public RedirectView saveMember(Article article, @RequestParam("image") MultipartFile file, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        if(result.hasErrors()) {
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person",result);

            // Add  person if invalid was received
            redirectAttributes.addFlashAttribute("article",article);
        }
        articleService.saveNew(article, file);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Success", "Person successfully added", FlashMessage.Status.SUCCESS));
        return new RedirectView("/Articles", true);
    }
}
