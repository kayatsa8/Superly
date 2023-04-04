package Domain.Business.Objects.Document;

import Domain.Service.util.ServiceDocumentFactory;
public abstract class Document {

    public Document() {
    }


    public abstract Domain.Service.Objects.Document.Document accept(ServiceDocumentFactory serviceDocumentFactory);
}
