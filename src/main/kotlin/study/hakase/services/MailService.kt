package study.hakase.services

import com.mailgun.api.v3.MailgunMessagesApi
import com.mailgun.client.MailgunClient
import com.mailgun.model.message.Message
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL
import java.nio.file.InvalidPathException
import java.nio.file.Paths


@Service
class MailService {
    @Autowired
    private lateinit var arXivMetadataService: ArXivMetadataService
    @Value("\${mailgun.api.key}")
    private lateinit var mailgunApiKey: String

    @Bean
    fun mailgunMessagesApi(): MailgunMessagesApi {
        println(this.mailgunApiKey)
        return MailgunClient.config(this.mailgunApiKey)
            .createApi(MailgunMessagesApi::class.java)!!
    }

    private fun validFilename(fileName: String): Boolean {
        return try {
            Paths.get(fileName)
            true
        } catch (e: InvalidPathException) {
            false
        }
    }

    private fun downloadPaper(site: String, ID: String): File {
        val link = arXivMetadataService.getPDFLink(ID)
        val title = arXivMetadataService.getTitle(ID)

        val fileName = if (this.validFilename("$title.pdf")) "${arXivMetadataService.getTitle(ID)}.pdf" else "$ID.pdf"
        val file = File(fileName)
        FileUtils.copyURLToFile(URL(link), file)

        return file
    }

    fun sendPaper(source: Pair<String, String>, email: String) {
        println(this.mailgunApiKey)
        val paper = this.downloadPaper(source.first, source.second)
        val message = Message.builder()
            .from("nano@hakase.study")
            .to(email)
            .subject(arXivMetadataService.getTitle(source.second))
            .text(arXivMetadataService.getJSON(source.second))
            .attachment(paper)
            .build()

        this.mailgunMessagesApi().sendMessage("hakase.study", message)
        paper.delete()
    }
}