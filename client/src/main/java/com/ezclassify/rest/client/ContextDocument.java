package com.ezclassify.rest.client;

import com.ezclassify.rest.contract.ContextDocumentAuditResult;
import com.ezclassify.rest.contract.DocumentResponseProfile;
import com.ezclassify.rest.contract.GuideDeterminationInput;
import com.ezclassify.rest.contract.Utf8Offsets;
import com.google.gson.JsonObject;

/** Document operations bound to one immutable server-resolved Context composition. */
public final class ContextDocument {
  private final EzClassifyContext ctx;

  ContextDocument(EzClassifyContext ctx) {
    this.ctx = ctx;
  }

  /**
   * Audits a document with the default {@link DocumentResponseProfile#STANDARD} projection.
   *
   * @param text exact document text
   * @return resolved Context envelope containing the structural and guide audit
   */
  public ContextDocumentAuditResult audit(String text) {
    return audit(text, null, DocumentResponseProfile.STANDARD);
  }

  /**
   * Audits a document using explicit analyst determinations and response projection.
   *
   * @param text exact document text
   * @param determinations optional accepted/rejected findings and manual selections
   * @param profile requested response detail profile
   * @return resolved Context envelope containing the structural and guide audit
   */
  public ContextDocumentAuditResult audit(
      String text, GuideDeterminationInput determinations, DocumentResponseProfile profile) {
    Utf8Offsets.requireValidUnicode(text);
    JsonObject body = new JsonObject();
    Json.put(body, "text", text);
    if (determinations != null)
      Json.put(body, "guideDeterminations", WireJson.toTree(determinations));
    Json.put(body, "responseProfile", requiredProfile(profile).toWire());
    return ctx.postTyped("document/audit", body, ContextDocumentAuditResult.class);
  }

  private static DocumentResponseProfile requiredProfile(DocumentResponseProfile profile) {
    if (profile == null) throw new IllegalArgumentException("response profile must not be null");
    return profile;
  }
}
