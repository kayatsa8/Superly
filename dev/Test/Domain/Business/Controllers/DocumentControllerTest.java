package Domain.Business.Controllers;

import Domain.Business.Controllers.Transport.DocumentController;
import Domain.Business.Objects.Document.DestinationDocument;
import Domain.Business.Objects.Document.TransportDocument;
import Domain.DAL.Controllers.TransportMudel.DestinationDocumentDAO;
import Domain.DAL.Controllers.TransportMudel.TransportDocumentDataMapper;
import junit.framework.TestCase;

import java.util.ArrayList;

public class DocumentControllerTest extends TestCase {
    static DocumentController controller;
    static DestinationDocumentDAO destinationDocumentDAO = new DestinationDocumentDAO();
    static TransportDocumentDataMapper transportDocument = new TransportDocumentDataMapper();
    private DestinationDocument dDoc;
    private TransportDocument tDoc;

    @Override
    public void setUp() throws Exception {
        controller = new DocumentController();
        dDoc  =  new DestinationDocument(303, 1, new ArrayList<>());
        tDoc  =  new TransportDocument(202, "18/01/2002", 123, "Chai");

    }

    @Override
    public void tearDown() throws Exception {
        destinationDocumentDAO.remove(303);
        transportDocument.remove(202);

    }

    public void testUploadDestinationDocument() {
        try {
            controller.uploadDestinationDocument(dDoc);
            dDoc = controller.getDestinationDocument(303);
            assertEquals(303, dDoc.getID());
        } catch (Exception e) {

        }
    }

    public void testGetDestinationDocument() {
        try {
            dDoc = controller.getDestinationDocument(303);
            assertEquals(303, dDoc.getID());
        } catch (Exception e) {

        }

    }

    public void testUploadTransportDocument() {
        try {
            controller.uploadTransportDocument(tDoc);
            tDoc = controller.getTransportDocument(202);
            assertEquals(202, dDoc.getID());
        } catch (Exception e) {

        }

    }

    public void testGetTransportDocument() {
        try {
            tDoc = controller.getTransportDocument(202);
            assertEquals(202, dDoc.getID());
        } catch (Exception e) {

        }

    }
}