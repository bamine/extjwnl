package net.didion.jwnl.dictionary.file;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.util.factory.Owned;
import net.didion.jwnl.util.factory.Param;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple container for <code>DictionaryCatalog</code>s that allows
 * a <code>DictionaryFile</code> to be retrieved by its <code>POS</code>
 * and <code>DictionaryFileType</code>.
 *
 * @author didion
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class DictionaryCatalogSet<E extends DictionaryFile> implements Owned {

    private Map<DictionaryFileType, DictionaryCatalog<E>> catalogs = new HashMap<DictionaryFileType, DictionaryCatalog<E>>();
    private Dictionary dictionary;

    /**
     * Creates a catalog set of the specified type of file using files in the specified dictionary directory.
     *
     * @param dictionary dictionary
     * @param params parameters
     * @param desiredDictionaryFileType desiredDictionaryFileType
     * @throws JWNLException JWNLException
     */
    public DictionaryCatalogSet(Dictionary dictionary, Map<String, Param> params, Class desiredDictionaryFileType) throws JWNLException {
        this.dictionary = dictionary;
        for (DictionaryFileType d : DictionaryFileType.getAllDictionaryFileTypes()) {
            DictionaryCatalog<E> cat = new DictionaryCatalog<E>(dictionary, d, desiredDictionaryFileType, params);
            catalogs.put(cat.getKey(), cat);
        }
    }

    public void open() throws IOException {
        if (!isOpen()) {
            for (Iterator<DictionaryCatalog<E>> itr = getCatalogIterator(); itr.hasNext();) {
                itr.next().open();
            }
        }
    }

    public void delete() throws IOException {
        for (Iterator<DictionaryCatalog<E>> itr = getCatalogIterator(); itr.hasNext();) {
            itr.next().delete();
        }
    }

    public boolean isOpen() {
        for (Iterator<DictionaryCatalog<E>> itr = getCatalogIterator(); itr.hasNext();) {
            if (!itr.next().isOpen()) {
                return false;
            }
        }
        return true;
    }

    public void close() {
        for (Iterator<DictionaryCatalog<E>> itr = getCatalogIterator(); itr.hasNext();) {
            itr.next().close();
        }
    }

    public DictionaryCatalog<E> get(DictionaryFileType fileType) {
        return catalogs.get(fileType);
    }

    public int size() {
        return catalogs.size();
    }

    public Iterator<DictionaryCatalog<E>> getCatalogIterator() {
        return catalogs.values().iterator();
    }

    public E getDictionaryFile(POS pos, DictionaryFileType fileType) {
        return get(fileType).get(pos);
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void save() throws IOException, JWNLException {
        catalogs.get(DictionaryFileType.EXCEPTION).save();
        catalogs.get(DictionaryFileType.DATA).save();
        catalogs.get(DictionaryFileType.INDEX).save();
    }

    public void edit() throws IOException {
        for (Iterator<DictionaryCatalog<E>> itr = getCatalogIterator(); itr.hasNext();) {
            itr.next().edit();
        }
    }
}