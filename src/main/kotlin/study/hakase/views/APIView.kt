package study.hakase.views

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import study.hakase.services.MailService

@RestController
@RequestMapping
class APIView {

    @Autowired
    private lateinit var mailService: MailService

    @GetMapping("/{site}/{id}")
    fun sendPaper(@PathVariable site: String, @PathVariable id: String, @RequestParam email: String) =
        mailService.sendPaper(Pair(site, id), email)

}