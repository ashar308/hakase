package study.hakase.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.ListItem
import com.vaadin.flow.component.html.OrderedList
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import study.hakase.services.ArXivMetadataService
import study.hakase.services.MailService
import study.hakase.services.SourceParsingService

@Route
class MainView(ams: ArXivMetadataService, sps: SourceParsingService, ms: MailService) : VerticalLayout() {

    private val steps = OrderedList()
    private val arXivMetadataService = ams
    private val sourceParsingService = sps
    private val mailService = ms

    init {
        val sourceField = TextField()
        sourceField.helperText = "arXiv ID or URL of Paper"
        sourceField.addFocusListener { }
        sourceField.addBlurListener {
            if (sourceField.value === null || sourceField.value === "") {
                sourceField.helperText = "arXiv ID or URL of Paper"
            } else {
                val source = sourceParsingService.parseSource(sourceField.value)
                if (source.first == "arXiv")sourceField.helperText = arXivMetadataService.getTitle(source.second)
            }
        }

        val emailField = EmailField()
        emailField.helperText = "Send-to-Kindle Email Address"

        val sendButton = Button("Send!")
        sendButton.addClickListener {
            val source = sourceParsingService.parseSource(sourceField.value)
            mailService.sendPaper(source, emailField.value)
        }

        steps.add(ListItem("If you are sending to a Kindle or Kindle App, add nano@hakase.study to your Approved Personal Document E-mail List."))
        steps.add(ListItem(sourceField))
        steps.add(ListItem(emailField))
        steps.add(ListItem(sendButton))

        add(
            H1("Hakase"),
            Paragraph("Send Research Papers to Kindle or Kindle App"),
            steps
        )
    }
}