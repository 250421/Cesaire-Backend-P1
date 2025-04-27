import { useAtom } from 'jotai';
import { atomWithStorage } from 'jotai/utils';
import { useEffect, useState } from 'react';

const sidebar = atomWithStorage('sidebar', true);

export const useSidebar = () => {
    const [isOpen, setIsOpen] = useAtom(sidebar);
    const [mounted, setMounted] = useState(false);

    useEffect(() => {
        setMounted(true);
    }, []);

    const toggle = () => setIsOpen((prev) => !prev);

    return {
        isOpen,
        mounted,
        toggle
    }
}