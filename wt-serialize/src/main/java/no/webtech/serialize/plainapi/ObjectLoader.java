package no.webtech.serialize.plainapi;

import java.io.Serializable;

import no.webtech.serialize.coreapi.QueryHolder;
import no.webtech.serialize.coreapi.SessionController;

public interface ObjectLoader<S extends Serializable, C extends SessionController, Q extends QueryHolder> {
	S loadObject(C sess, Q query);
}