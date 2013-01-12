package no.webtech.serialize.kastes.gammelresthttp;
//package no.webtech.serialize.rvoapi;
//
//
//
//
//public class Q2RResolver {
//	
//	private static Q2R impl;
//
//	public static Q2R getInstance() {
//		if (impl == null)
//			impl = getInstance(Q2R.defaultClassNameForImplDoNotUse);
//		return impl;
//	}
//
//	public static Q2R getInstance(String classNameForImpl) {
//		if (impl == null) {
//			try {
//				impl = (Q2R) Class.forName(classNameForImpl).newInstance();
//			} catch (ClassNotFoundException e) {
//				throw new RuntimeException(e);
//			} catch (InstantiationException e) {
//				throw new RuntimeException(e);
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return impl;
//	}
//}
