package no.webtech.serialize.resthttppoc;

import no.webtech.serialize.coreapi.SessionController;

public interface PocHttpSessionController extends SessionController {
	int getPort();
	String getHost();
}
